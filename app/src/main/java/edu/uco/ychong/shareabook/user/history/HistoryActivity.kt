package edu.uco.ychong.shareabook.user.tracking

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import edu.uco.ychong.shareabook.EXTRA_HISTORY_TAB
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.user.history.HistoryTabAdapter
import edu.uco.ychong.shareabook.user.history.checkout.CheckedOutFragment
import edu.uco.ychong.shareabook.user.history.lended.LendedFragment
import edu.uco.ychong.shareabook.user.history.returned.ReturnedFragment
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyTrackingAdapter: HistoryTabAdapter

    private var tabIcons = arrayListOf(
            R.drawable.ic_shoppingcart_white,
            R.drawable.ic_return_book_white,
            R.drawable.ic_history_white
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        removeActionBarShadow()

        historyTrackingAdapter = HistoryTabAdapter(supportFragmentManager)
        historyTrackingAdapter.addFragment(CheckedOutFragment(), "Checked Out")
        historyTrackingAdapter.addFragment(ReturnedFragment(), "Returned")
        historyTrackingAdapter.addFragment(LendedFragment(), "Lended")

        id_viewPager.adapter = historyTrackingAdapter
        id_tabLayout.setupWithViewPager(id_viewPager)

        id_tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        id_tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
        id_tabLayout.getTabAt(2)?.setIcon(tabIcons[2])

        handleInitialTab()
    }

    private fun handleInitialTab() {
        val tab = intent.getStringExtra(EXTRA_HISTORY_TAB)
        when (tab) {
            "returned" -> id_viewPager.currentItem = 1
            "checked_out" -> id_viewPager.currentItem = 0
            else -> id_viewPager.currentItem = 0
        }
    }

    private fun removeActionBarShadow() {
        supportActionBar?.elevation = 0.0f
    }

}
