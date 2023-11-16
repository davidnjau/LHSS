/*
 * Copyright 2022-2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.demo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.demo.data.FormatterClass
import com.google.android.fhir.demo.databinding.PatientDetailBinding

/**
 * A fragment representing a single Patient detail screen. This fragment is contained in a
 * [MainActivity].
 */
class PatientDetailsFragment : Fragment() {
  private lateinit var fhirEngine: FhirEngine
  private lateinit var patientDetailsViewModel: PatientDetailsViewModel
  private val args: PatientDetailsFragmentArgs by navArgs()
  private var _binding: PatientDetailBinding? = null
  private val binding
    get() = _binding!!

  var editMenuItem: MenuItem? = null
  private val formatterClass = FormatterClass()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = PatientDetailBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    fhirEngine = FhirApplication.fhirEngine(requireContext())
    patientDetailsViewModel =
      ViewModelProvider(
          this,
          PatientDetailsViewModelFactory(requireActivity().application, fhirEngine, args.patientId),
        )
        .get(PatientDetailsViewModel::class.java)
    val adapter = PatientDetailsRecyclerViewAdapter(::onAddScreenerClick, ::onVitalsClick, ::onReferralClick )
    binding.recycler.adapter = adapter
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = "Patient Card"
      setDisplayHomeAsUpEnabled(true)
    }
    patientDetailsViewModel.livePatientData.observe(viewLifecycleOwner) {
      adapter.submitList(it)
      if (!it.isNullOrEmpty()) {
        editMenuItem?.isEnabled = true
      }
    }



    patientDetailsViewModel.getPatientDetailData()
    (activity as MainActivity).setDrawerEnabled(false)

    Log.e("---------", args.patientId)

  }

  private fun onAddScreenerClick() {

    formatterClass.saveSharedPref(
      "questionnaireJson",
      "screening.json",
      requireContext())

    findNavController()
      .navigate(
        PatientDetailsFragmentDirections.actionPatientDetailsToScreenEncounterFragment(
          args.patientId,
        ),
      )

  }
  private fun onVitalsClick() {

    formatterClass.saveSharedPref(
      "questionnaireJson",
      "capture-vitals.json",
      requireContext())

    findNavController()
      .navigate(
        PatientDetailsFragmentDirections.actionPatientDetailsToScreenEncounterFragment(
          args.patientId,
        ),
      )

  }
  private fun onReferralClick() {

    formatterClass.saveSharedPref(
      "questionnaireJson",
      "referral.json",
      requireContext())

    findNavController()
      .navigate(
        PatientDetailsFragmentDirections.actionPatientDetailsToScreenEncounterFragment(
          args.patientId,
        ),
      )

  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.details_options_menu, menu)
    editMenuItem = menu.findItem(R.id.menu_patient_edit)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        NavHostFragment.findNavController(this).navigateUp()
        true
      }
      R.id.menu_patient_edit -> {
        findNavController()
          .navigate(PatientDetailsFragmentDirections.navigateToEditPatient(args.patientId))
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
