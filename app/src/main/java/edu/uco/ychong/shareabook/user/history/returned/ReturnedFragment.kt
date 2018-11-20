package edu.uco.ychong.shareabook.user.history.returned

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.uco.ychong.shareabook.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ReturnedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_returned, container, false)



    }
}
