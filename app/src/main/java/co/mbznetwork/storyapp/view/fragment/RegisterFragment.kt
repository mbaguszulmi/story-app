package co.mbznetwork.storyapp.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import co.mbznetwork.storyapp.R
import co.mbznetwork.storyapp.databinding.FragmentRegisterBinding
import co.mbznetwork.storyapp.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    private val registerViewModel by viewModels<RegisterViewModel>()

    override val layoutId: Int
        get() = R.layout.fragment_register

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        requireBinding {
            lifecycleOwner = viewLifecycleOwner
            vm = registerViewModel
            tilName.errorChangedListener = {
                registerViewModel.setNameHasError(it)
            }
            tilEmail.errorChangedListener = {
                registerViewModel.setEmailHasError(it)
            }
            tilPassword.errorChangedListener = {
                registerViewModel.setPasswordHasError(it)
            }
            btnRegister.setOnClickListener {
                registerViewModel.register(
                    edRegisterName.text?.toString() ?: "",
                    edRegisterEmail.text?.toString() ?: "",
                    edRegisterPassword.text?.toString() ?: ""
                )
            }
        }
    }
}