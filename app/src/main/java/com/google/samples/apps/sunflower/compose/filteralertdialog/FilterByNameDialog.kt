/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.compose.filteralertdialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.samples.apps.sunflower.R

class FilterByNameDialog(val onApplyFilter: (name: String) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // usually I prefer to create the entire layout in xml, but as I simply wanted an EditText, I opted by directly creating it here
        val input = EditText(requireActivity())
        input.hint = getString(R.string.text_to_search)
        input.inputType = InputType.TYPE_CLASS_TEXT
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.filter_by_name))
            .setView(input)
            .setPositiveButton(getString(android.R.string.ok)) { _,_ -> onApplyFilter(input.text.toString())}
            .create()
    }
}