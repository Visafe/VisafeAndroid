package com.vn.visafe_android.ui.authentication.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.vn.visafe_android.R

class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_splash, container, false)
        val tvTitle: TextView = root.findViewById(R.id.title)
        pageViewModel.title.observe(viewLifecycleOwner, Observer<Int> {
            tvTitle.text = requireContext().resources.getString(it)
        })

        val tvContent: TextView = root.findViewById(R.id.content)
        pageViewModel.content.observe(viewLifecycleOwner, Observer<Int> {
            tvContent.text = requireContext().resources.getString(it)
        })

        val imgLogo: ImageView = root.findViewById(R.id.imgLogo)
        pageViewModel.resourceId.observe(viewLifecycleOwner, Observer<Int> {
            if (it != 0) {
                imgLogo.setImageDrawable(context?.let { it1 -> ContextCompat.getDrawable(it1, it) })
            } else {
                imgLogo.visibility = View.INVISIBLE
            }
        })
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}