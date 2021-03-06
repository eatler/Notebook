package com.example.notebook.addedit.edit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.notebook.addedit.AddEditContract
import com.example.notebook.addedit.AddEditPresenter
import com.example.notebook.databinding.FragmentAddEditBinding
import com.example.notebook.databinding.PhoneNumberEditItemBinding
import com.example.notebook.databinding.SocialMediaEditItemBinding
import com.example.notebook.db.Contact
import com.example.notebook.main.MainActivity
import com.example.notebook.utils.NoWhiteSpaceFilter
import javax.inject.Inject

class EditFragment : Fragment(), AddEditContract.View {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!
    private var contact: Contact? = null

    @Inject
    lateinit var presenter: AddEditPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.addComponent().create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBinding.inflate(inflater, container, false)
        initView()
        presenter.bindView(this)
        presenter.onViewCreated()
        if (savedInstanceState != null) {
            contact = savedInstanceState.getParcelable(SAVED_CONTACT)
            presenter.fillView(contact)
        } else {
            contact = requireArguments().getParcelable(EDIT_CONTACT_EXTRA)
            presenter.fillView(contact)
        }
        return binding.root
    }

    private fun initView() {
        binding.firstNameEditText.filters = arrayOf(NoWhiteSpaceFilter)
        binding.lastNameEditText.filters = arrayOf(NoWhiteSpaceFilter)
        binding.middleNameEditText.filters = arrayOf(NoWhiteSpaceFilter)
        binding.addEditToolbarLayout.addEditToolbar.setNavigationOnClickListener {
            presenter.onBackPressed()
        }
        binding.addEditToolbarLayout.confirmButton.setOnClickListener {
            presenter.onConfirmEditClicked(createContact(contact?.id))
        }
        binding.addFab.setOnClickListener {
            presenter.onAddFabClicked(binding.addFab.isExtended)
        }
        binding.addLinkFab.setOnClickListener {
            presenter.onAddLinkClicked()
        }
        binding.addPhoneFab.setOnClickListener {
            presenter.onAddPhoneClicked()
        }

    }

    override fun shrinkAddFab() {
        binding.addFab.shrink()
    }

    override fun hideNestedFabs() {
        binding.addPhoneFab.visibility = View.GONE
        binding.addLinkFab.visibility = View.GONE
        binding.addLinkActionText.visibility = View.GONE
        binding.addPersonActionText.visibility = View.GONE
    }

    override fun extendAddFab() {
        binding.addFab.extend()
    }

    override fun showNestedFabs() {
        binding.addPhoneFab.visibility = View.VISIBLE
        binding.addLinkFab.visibility = View.VISIBLE
        binding.addLinkActionText.visibility = View.VISIBLE
        binding.addPersonActionText.visibility = View.VISIBLE
    }

    override fun addSocialMediaItem(link: String?) {
        val socialMediaEditItemBinding =
            SocialMediaEditItemBinding.inflate(
                layoutInflater,
                binding.socialMediaLayout.socialMediaItemsLayout,
                false
            )
        socialMediaEditItemBinding.root.setText(link)
        binding.socialMediaLayout.socialMediaItemsLayout.addView(socialMediaEditItemBinding.root)
    }

    override fun addPhoneItem(phone: String?) {
        val phoneNumberItemBinding =
            PhoneNumberEditItemBinding.inflate(
                layoutInflater,
                binding.phoneNumbersLayout.phoneNumbersItemsLayout,
                false
            )
        phoneNumberItemBinding.root.setText(phone)
        binding.phoneNumbersLayout.phoneNumbersItemsLayout.addView(phoneNumberItemBinding.root)
    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun fillFirstName(firstName: String?) {
        binding.firstNameEditText.setText(firstName)
    }

    override fun fillLastName(lastName: String?) {
        binding.lastNameEditText.setText(lastName)
    }

    override fun fillMiddleName(middleName: String?) {
        binding.middleNameEditText.setText(middleName)
    }

    override fun fillPicUrl(picUrl: String?) {
        binding.picUrlEditText.setText(picUrl)
    }

    override fun fillDescription(description: String?) {
        binding.descriptionEditText.setText(description)
    }

    private fun createContact(id: Int?) = Contact(
        binding.firstNameEditText.text.toString(),
        binding.lastNameEditText.text.toString().ifEmpty { null },
        binding.middleNameEditText.text.toString().ifEmpty { null },
        binding.picUrlEditText.text.toString().ifEmpty { null },
        binding.phoneNumbersLayout.phoneNumbersItemsLayout.children.toList().ifEmpty { null }
            ?.map { (it as TextView).text.toString() }?.filter { it.isNotEmpty() },
        binding.socialMediaLayout.socialMediaItemsLayout.children.toList().ifEmpty { null }
            ?.map { (it as TextView).text.toString() }?.filter { it.isNotEmpty() },
        binding.descriptionEditText.text.toString().ifEmpty { null },
        id
    )

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SAVED_CONTACT, createContact(contact?.id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unBindView()
        _binding = null
    }

    companion object {
        private const val SAVED_CONTACT = "SAVED_CONTACT"
        private const val EDIT_CONTACT_EXTRA = "EDIT_CONTACT_EXTRA"
        const val FRAGMENT_EDIT_TAG = "FRAGMENT_EDIT_TAG"

        @JvmStatic
        fun newInstance(contact: Contact) = EditFragment().also {
            it.arguments = Bundle().apply {
                putParcelable(EDIT_CONTACT_EXTRA, contact)
            }
        }
    }
}