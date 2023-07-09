package co.mbznetwork.storyapp.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import co.mbznetwork.storyapp.R
import co.mbznetwork.storyapp.databinding.FragmentLoginBinding
import co.mbznetwork.storyapp.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val loginViewModel by viewModels<LoginViewModel>()

    override val layoutId: Int
        get() = R.layout.fragment_login

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        requireBinding {
            lifecycleOwner = viewLifecycleOwner
            vm = loginViewModel
            tilEmail.errorChangedListener = {
                loginViewModel.setEmailHasError(it)
            }
            tilPassword.errorChangedListener = {
                loginViewModel.setPasswordHasError(it)
            }
            btnLogin.setOnClickListener {
                loginViewModel.login(
                    edLoginEmail.text?.toString() ?: "",
                    edLoginPassword.text?.toString() ?: ""
                )
            }
        }
    }
}
