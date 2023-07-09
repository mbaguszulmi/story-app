package co.mbznetwork.storyapp.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import co.mbznetwork.storyapp.model.ui.UiMessage
import co.mbznetwork.storyapp.model.ui.UiStatus
import co.mbznetwork.storyapp.util.LoadingDialogUtil
import co.mbznetwork.storyapp.util.MessageDialog
import co.mbznetwork.storyapp.view.fragment.LoadingOverlay
import co.mbznetwork.storyapp.viewmodel.UiStatusViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseActivity<T: ViewDataBinding>: AppCompatActivity() {

    private val uiStatusViewModel by viewModels<UiStatusViewModel>()
    private var loadingDialog: LoadingOverlay? = null
    private var allowUiState = true
    protected lateinit var binding: T

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        observeUiStatus()
    }

    private fun observeUiStatus() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                uiStatusViewModel.uiStatus.collectLatest {
                    if (allowUiState) {
                        val root: View = window.decorView.findViewById(android.R.id.content)
                        when (it) {
                            is UiStatus.Idle -> removeLoadingDialog()
                            is UiStatus.Loading -> showLoadingDialog()
                            is UiStatus.ShowError -> {
                                removeLoadingDialog()
                                it.errorMsg.let { message ->
                                    MessageDialog.showError(root, root, when (message) {
                                        is UiMessage.StringMessage -> {
                                            message.message
                                        }
                                        is UiMessage.ResourceMessage -> {
                                            getString(message.id, *message.formatArgs)
                                        }
                                    })
                                }
                            }
                            is UiStatus.ShowMessage -> {
                                removeLoadingDialog()
                                it.message.let { message ->
                                    MessageDialog.showMessage(root, root, when (message) {
                                        is UiMessage.StringMessage -> {
                                            message.message
                                        }
                                        is UiMessage.ResourceMessage -> {
                                            getString(message.id, *message.formatArgs)
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun removeLoadingDialog() {
        loadingDialog?.let { l ->
            LoadingDialogUtil.dismiss(l)
        }
        loadingDialog = null
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null)
            loadingDialog = LoadingDialogUtil.show(supportFragmentManager)
    }

    override fun onResume() {
        super.onResume()
        allowUiState = true
    }

    override fun onPause() {
        removeLoadingDialog()
        allowUiState = false
        super.onPause()
    }
}