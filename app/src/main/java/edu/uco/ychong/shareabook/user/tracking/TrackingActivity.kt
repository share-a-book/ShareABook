package edu.uco.ychong.shareabook.user.tracking

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import edu.uco.ychong.shareabook.EXTRA_TRACKING_TAB
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.user.tracking.fragments.confirm.ConfirmFragment
import edu.uco.ychong.shareabook.user.tracking.fragments.pending.PendingFragment
import edu.uco.ychong.shareabook.user.tracking.fragments.request.RequestFragment
import kotlinx.android.synthetic.main.activity_tracking.*

class TrackingActivity : AppCompatActivity() {

    private lateinit var trackingTabAdapter: TrackingTabAdapter

    private var tabIcons = arrayListOf(
            R.drawable.ic_hourglass_white_24,
            R.drawable.ic_confirm_white_24,
            R.drawable.ic_ask_question_24
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        removeActionBarShadow()

        trackingTabAdapter = TrackingTabAdapter(supportFragmentManager)
        trackingTabAdapter.addFragment(PendingFragment(), "Pending")
        trackingTabAdapter.addFragment(ConfirmFragment(), "Confirmed")
        trackingTabAdapter.addFragment(RequestFragment(), "Request")

        id_viewPager.adapter = trackingTabAdapter
        id_tabLayout.setupWithViewPager(id_viewPager)

        id_tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        id_tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
        id_tabLayout.getTabAt(2)?.setIcon(tabIcons[2])

        handleInitialTab()
    }

    private fun handleInitialTab() {
        val tab = intent.getStringExtra(EXTRA_TRACKING_TAB)
        when (tab) {
            "pending" -> id_viewPager.currentItem = 0
            "confirmed" -> id_viewPager.currentItem = 1
            "request" -> id_viewPager.currentItem = 2
            else -> id_viewPager.currentItem = 0
        }
    }

    private fun removeActionBarShadow() {
        supportActionBar?.elevation = 0.0f
    }

}
