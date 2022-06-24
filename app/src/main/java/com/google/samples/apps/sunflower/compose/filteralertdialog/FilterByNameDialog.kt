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
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.samples.apps.sunflower.R

class FilterByNameDialog(val onApplyFilter: (name: String) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireActivity()).inflate(R.layout.layout_filter_by_name, null)
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.filter_by_name))
            .setView(view)
            .setPositiveButton(getString(android.R.string.ok)) { _,_ -> onApplyFilter(view.findViewById<EditText>(R.id.edit_text).text.toString())}
            .create()
    }
}