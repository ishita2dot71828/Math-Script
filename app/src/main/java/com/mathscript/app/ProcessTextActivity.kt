package com.mathscript.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class ProcessTextActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
        val isReadOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)

        if (selectedText != null && !isReadOnly) {
            val converted = ScriptMappings.convertMathScript(selectedText)
            val resultIntent = Intent().apply {
                putExtra(Intent.EXTRA_PROCESS_TEXT, converted)
            }
            setResult(RESULT_OK, resultIntent)
        }
        finish()
    }
}
