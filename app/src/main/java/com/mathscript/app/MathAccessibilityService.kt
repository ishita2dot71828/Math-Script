package com.mathscript.app

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityButtonController
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.service.quicksettings.TileService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class MathAccessibilityService : AccessibilityService() {

    companion object {
        var instance: MathAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            accessibilityButtonController.registerAccessibilityButtonCallback(
                object : AccessibilityButtonController.AccessibilityButtonCallback() {
                    override fun onClicked(controller: AccessibilityButtonController?) {
                        convertCurrentSelection()
                    }
                }
            )
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    fun convertCurrentSelection() {
        val root = rootInActiveWindow ?: run {
            Toast.makeText(this, "Turn on Math Script in Accessibility Settings first", Toast.LENGTH_SHORT).show()
            return
        }

        val targetNode = findSelectedOrFocusedNode(root)
        if (targetNode != null && !targetNode.text.isNullOrEmpty()) {
            val fullText = targetNode.text.toString()
            val start = targetNode.textSelectionStart
            val end = targetNode.textSelectionEnd

            val hasSelection = start >= 0 && end >= 0 && start != end
            val min = if (hasSelection) minOf(start, end) else 0
            val max = if (hasSelection) maxOf(start, end) else fullText.length

            val selectedSub = fullText.substring(min, max)
            val convertedSub = ScriptMappings.convertMathScript(selectedSub)
            val newFullText = fullText.substring(0, min) + convertedSub + fullText.substring(max)

            val args = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, newFullText)
            }
            val replaced = targetNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)

            if (!replaced) {
                copyToClipboard(convertedSub)
                Toast.makeText(this, "Copied: $convertedSub (Tap Paste)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Converted!", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Fallback: convert whatever is currently in the clipboard
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

    private fun findSelectedOrFocusedNode(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (node.isEditable && (node.isFocused || (node.textSelectionStart != node.textSelectionEnd && node.textSelectionStart >= 0))) {
            return node
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findSelectedOrFocusedNode(child)
            if (found != null) return found
        }
        return null
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("MathScript", text))
    }
}

class MathTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val service = MathAccessibilityService.instance
        if (service != null) {
            service.convertCurrentSelection()
        } else {
            Toast.makeText(this, "Enable Math Script in Settings -> Accessibility -> Installed apps", Toast.LENGTH_LONG).show()
        }
    }
}
