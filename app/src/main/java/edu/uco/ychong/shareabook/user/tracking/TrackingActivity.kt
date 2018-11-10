package edu.uco.ychong.shareabook.user.tracking

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.user.tracking.fragments.confirm.ConfirmFragment
import edu.uco.ychong.shareabook.user.tracking.fragments.pending.PendingFragment
import edu.uco.ychong.shareabook.user.tracking.fragments.request.RequestIncomingFragment
import kotlinx.android.synthetic.main.activity_tracking.*

class TrackingActivity : AppCompatActivity() {

    private lateinit var trackingTabAdapter: TrackingTabAdapter

    private var tabIcons = arrayListOf(
            R.drawable.ic_hourglass_white_24,
            R.drawable.ic_ask_question_24,
            R.drawable.ic_confirm_white_24
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        removeActionBarShadow()

        trackingTabAdapter = TrackingTabAdapter(supportFragmentManager)
        trackingTabAdapter.addFragment(PendingFragment(), "Pending")
        trackingTabAdapter.addFragment(RequestIncomingFragment(), "Request")
        trackingTabAdapter.addFragment(ConfirmFragment(), "Confirmed")

        id_viewPager.adapter = trackingTabAdapter
        id_tabLayout.setupWithViewPager(id_viewPager)

        id_tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        id_tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
        id_tabLayout.getTabAt(2)?.setIcon(tabIcons[2])
    }

    private fun removeActionBarShadow() {
        supportActionBar?.elevation = 0.0f
    }

}
