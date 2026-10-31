package com.mathscript.app

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

class ProcessTextActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
            ?: intent.getStringExtra(Intent.EXTRA_TEXT)

        if (!selectedText.isNullOrEmpty()) {
            val trimmedText = selectedText.trimEnd('\n', '\r')
            val converted = ScriptMappings.convertMathScript(trimmedText)
            val isReadOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)

            // Always copy to clipboard as fallback for Samsung Notes / WhatsApp Share
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("MathScript", converted))

            if (!isReadOnly && intent.action == Intent.ACTION_PROCESS_TEXT) {
                val resultIntent = Intent().apply {
                    putExtra(Intent.EXTRA_PROCESS_TEXT, converted)
                }
                setResult(RESULT_OK, resultIntent)
            } else {
                Toast.makeText(this, "Copied: $converted", Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }
}
