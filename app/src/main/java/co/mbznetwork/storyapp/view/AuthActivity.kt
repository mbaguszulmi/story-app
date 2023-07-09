package co.mbznetwork.storyapp.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import co.mbznetwork.storyapp.R
import co.mbznetwork.storyapp.databinding.ActivityAuthBinding
import co.mbznetwork.storyapp.util.activityLifecycle
import co.mbznetwork.storyapp.view.fragment.LoginFragment
import co.mbznetwork.storyapp.view.fragment.RegisterFragment
import co.mbznetwork.storyapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AuthActivity : BaseActivity<ActivityAuthBinding>() {
    private val authViewModel by viewModels<AuthViewModel>()

    override val layoutId: Int
        get() = R.layout.activity_auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        observePage()
        observeAuthStatus()
    }

    private fun initView() {
        supportActionBar?.hide()
    }

    private fun observePage() {
        activityLifecycle {
            authViewModel.isLoginPage.collectLatest {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fcv_auth,
                    if (it) LoginFragment()
                    else RegisterFragment()
                ).commit()
            }
        }
    }

    private fun observeAuthStatus() {
        activityLifecycle {
            authViewModel.isAuthenticated.collectLatest {
                if (it) startActivity(
                    Intent(this@AuthActivity, MainActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                )
            }
        }
    }
}