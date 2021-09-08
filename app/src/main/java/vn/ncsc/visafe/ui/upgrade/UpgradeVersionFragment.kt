package vn.ncsc.visafe.ui.upgrade

import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.FragmentUpgradeVersionBinding
import vn.ncsc.visafe.model.request.OderPayRequest
import vn.ncsc.visafe.model.response.AllPackageResponse
import vn.ncsc.visafe.model.response.PayPackageResponse
import vn.ncsc.visafe.model.response.PriceAllPackage
import vn.ncsc.visafe.model.response.StatsWorkspaceResponse
import vn.ncsc.visafe.ui.WebViewActivity
import vn.ncsc.visafe.ui.adapter.UpgradeAdapter
import vn.ncsc.visafe.ui.adapter.UpgradePricesAdapter
import vn.ncsc.visafe.utils.*

class UpgradeVersionFragment : BaseFragment<FragmentUpgradeVersionBinding>(), UpgradePricesAdapter.OnClickPay {

    companion object {
        fun newInstance(type: String, response: AllPackageResponse?): UpgradeVersionFragment {
            val fragment = UpgradeVersionFragment()
            fragment.arguments = bundleOf(
                Pair(UpgradeActivity.UPGRADE_KEY, type),
                Pair(UpgradeActivity.DATA_PACKAGE, response)
            )
            return fragment
        }
    }

    private var allPackageResponse: AllPackageResponse? = null
    private var type = ""

    override fun layoutRes(): Int = R.layout.fragment_upgrade_version

    override fun initView() {
        type = arguments?.getString(UpgradeActivity.UPGRADE_KEY, "") ?: ""
        allPackageResponse = arguments?.getParcelable(UpgradeActivity.DATA_PACKAGE)
        allPackageResponse?.let {
            if (type.isNotEmpty()) {
                when (type) {
                    UpgradeActivity.TYPE_PREMIUM -> {
                        binding.tvUpgradeStatus.text = type
                        binding.tvUpgradeStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_subtract, 0, 0, 0)
                        binding.tvUpgradeStatus.setBackgroundTint(R.color.color_FFB31F)
                        binding.tvUpgradeStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_0641448))
                        binding.tvUpgradeContent.text = getString(R.string.premium_content)
                        binding.rvData.adapter = UpgradeAdapter(premiumList(requireContext()))
                        binding.rcvListPrices.adapter = it.prices?.let { it1 -> UpgradePricesAdapter(it1, this) }
                    }
                    UpgradeActivity.TYPE_FAMILY -> {
                        binding.tvUpgradeStatus.text = type
                        binding.tvUpgradeStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_subtract_white,
                            0,
                            0,
                            0
                        )
                        binding.tvUpgradeStatus.setBackgroundTint(R.color.color_15A1FA)
                        binding.tvUpgradeStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        binding.tvUpgradeContent.text = getString(R.string.family_content)
                        binding.rvData.adapter = UpgradeAdapter(familyList(requireContext()))
                        binding.rcvListPrices.adapter = it.prices?.let { it1 -> UpgradePricesAdapter(it1, this) }
                    }
                    UpgradeActivity.TYPE_BUSINESS -> {
                        binding.tvUpgradeStatus.text = type
                        binding.tvUpgradeStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_subtract_white,
                            0,
                            0,
                            0
                        )
                        binding.tvUpgradeStatus.setBackgroundTint(R.color.color_FF4451)
                        binding.tvUpgradeStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        binding.tvUpgradeContent.text = getString(R.string.bussiness_content)
                        binding.rvData.adapter = UpgradeAdapter(businessList(requireContext()))
                        val listPrice: MutableList<PriceAllPackage> = mutableListOf()
                        listPrice.clear()
                        listPrice.add(PriceAllPackage(-1, 12, 90, -1))
                        listPrice.add(PriceAllPackage(-1, 1, 7, -1))
                        binding.rcvListPrices.adapter = UpgradePricesAdapter(listPrice, this)
                    }
                }
            }
        }
    }

    override fun onPay(item: PriceAllPackage, position: Int) {
        if (type.isNotEmpty()) {
            when (type) {
                UpgradeActivity.TYPE_PREMIUM -> {
                    payPackage(item)
                }
                UpgradeActivity.TYPE_FAMILY -> {
                    payPackage(item)
                }
                UpgradeActivity.TYPE_BUSINESS -> {
                    activity?.let { Utils.callPhone(getString(R.string.hotline), it) }
                }
            }
        }
    }

    private fun payPackage(item: PriceAllPackage) {
        showProgressDialog()
        val deviceId = SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(PreferenceKey.DEVICE_ID)
        val oderPayRequest = OderPayRequest(package_price_time_id = item.id, deviceId)
        val client = NetworkClient()
        val call =
            client.client(context = requireContext())
                .doOderPayPackage(oderPayRequest)
        call.enqueue(BaseCallback(this, object : Callback<PayPackageResponse> {
            override fun onResponse(
                call: Call<PayPackageResponse>,
                response: Response<PayPackageResponse>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    response.body()?.let { data ->
                        activity?.let { data.payUrl?.let { it1 -> Utils.openWebsite(it1, it) } }
                    }
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<PayPackageResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

}