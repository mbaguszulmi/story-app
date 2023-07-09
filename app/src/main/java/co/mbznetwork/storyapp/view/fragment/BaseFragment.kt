package co.mbznetwork.storyapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T: ViewDataBinding>: Fragment() {
    abstract val layoutId: Int

    protected var binding: T? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DataBindingUtil.inflate<T>(inflater, layoutId, container, false).also {
            binding = it
        }.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    fun requireBinding(block: T.() -> Unit) {
        binding?.block()
    }
}
