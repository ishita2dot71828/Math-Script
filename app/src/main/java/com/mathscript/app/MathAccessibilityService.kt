package com.mathscript.app

import android.accessibilityservice.AccessibilityService
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class MathAccessibilityService : AccessibilityService() {

    companion object {
        var instance: MathAccessibilityService? = null
    }

    private var lastEditableNode: AccessibilityNodeInfo? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return
        // Ignore SystemUI so pulling down Quick Settings doesn't overwrite the target field
        if (pkg == "com.android.systemui") return

        val source = event.source ?: return
        if (source.isEditable || hasActiveSelection(source)) {
            lastEditableNode = source
        }
    }

    override fun onInterrupt() {}

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        lastEditableNode = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        lastEditableNode = null
    }

    fun convertCurrentSelection() {
        val targetNode = findBestTargetNode()

        if (targetNode != null && !targetNode.text.isNullOrEmpty()) {
            val fullText = targetNode.text.toString()
            val start = targetNode.textSelectionStart
            val end = targetNode.textSelectionEnd

            val hasSelection = start >= 0 && end >= 0 && start != end
            val min = if (hasSelection) minOf(start, end).coerceAtMost(fullText.length) else 0
            val max = if (hasSelection) maxOf(start, end).coerceAtMost(fullText.length) else fullText.length

            val selectedSub = fullText.substring(min, max)
            val convertedSub = ScriptMappings.convertMathScript(selectedSub)
            val newFullText = fullText.substring(0, min) + convertedSub + fullText.substring(max)

            // 1. Try direct in-place replacement first
            val setArgs = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, newFullText)
            }
            val replaced = targetNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, setArgs)

            if (replaced) {
                Toast.makeText(this, "Converted!", Toast.LENGTH_SHORT).show()
                return
            }

            // 2. Fallback for Samsung Notes, Reddit, Obsidian, Discord:
            // Copy the converted piece, keep the target range highlighted, and trigger ACTION_PASTE
            copyToClipboard(convertedSub)

            val selArgs = Bundle().apply {
                putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, min)
                putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, max)
            }
            targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
            targetNode.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, selArgs)

            val pasted = targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE)
            if (pasted) {
                Toast.makeText(this, "Converted & Pasted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Copied: $convertedSub (Tap Paste to replace)", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 3. Final fallback: convert whatever is currently in the clipboard
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
            if (!clipText.isNullOrEmpty()) {
                val converted = ScriptMappings.convertMathScript(clipText)
                copyToClipboard(converted)
                Toast.makeText(this, "Clipboard converted: $converted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Highlight or copy text first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun findBestTargetNode(): AccessibilityNodeInfo? {
        val roots = mutableListOf<AccessibilityNodeInfo>()

        // Search all interactive windows (ignores SystemUI shade when Quick Settings is pulled down)
        for (window in windows) {
            val root = window.root ?: continue
            if (root.packageName?.toString() != "com.android.systemui") {
                roots.add(root)
            }
        }
        rootInActiveWindow?.let { root ->
            if (root.packageName?.toString() != "com.android.systemui" && !roots.contains(root)) {
                roots.add(root)
            }
        }

        // Pass 1: Look for an actively focused or selected editable/WebView node
        for (root in roots) {
            findSelectedOrFocusedNode(root, requireFocusOrSelection = true)?.let { return it }
        }

        // Pass 2: Use the last tracked node before the Quick Settings shade was pulled down
        lastEditableNode?.let { cached ->
            cached.refresh()
            if (!cached.text.isNullOrEmpty()) return cached
        }

        // Pass 3: Look for any editable field with text on screen (catches unfocused Instagram/Discord bars)
        for (root in roots) {
            findSelectedOrFocusedNode(root, requireFocusOrSelection = false)?.let { return it }
        }

        return null
    }

    private fun findSelectedOrFocusedNode(
        node: AccessibilityNodeInfo,
        requireFocusOrSelection: Boolean
    ): AccessibilityNodeInfo? {
        val selected = hasActiveSelection(node)
        if (requireFocusOrSelection) {
            if ((node.isEditable && (node.isFocused || selected)) || (selected && !node.text.isNullOrEmpty())) {
                return node
            }
        } else {
            if (node.isEditable && !node.text.isNullOrEmpty()) {
                return node
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findSelectedOrFocusedNode(child, requireFocusOrSelection)
            if (found != null) return found
        }
        return null
    }

    private fun hasActiveSelection(node: AccessibilityNodeInfo): Boolean {
        val start = node.textSelectionStart
        val end = node.textSelectionEnd
        return start >= 0 && end >= 0 && start != end
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("MathScript", text))
    }
}

class MathTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        syncTileState()
    }

    override fun onClick() {
        super.onClick()
        val service = MathAccessibilityService.instance
        if (service != null) {
            service.convertCurrentSelection()
            syncTileState()
        } else {
            Toast.makeText(
                this,
                "Enable Math Script in Accessibility Settings",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivityAndCollapse(intent)
        }
    }

    private fun syncTileState() {
        val tile = qsTile ?: return
        tile.state = if (MathAccessibilityService.instance != null) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }
        tile.updateTile()
    }
}
