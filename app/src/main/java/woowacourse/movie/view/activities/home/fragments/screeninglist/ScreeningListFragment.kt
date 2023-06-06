package woowacourse.movie.view.activities.home.fragments.screeninglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import woowacourse.movie.R
import woowacourse.movie.databinding.FragmentScreeningListBinding
import woowacourse.movie.databinding.ItemTheaterBinding
import woowacourse.movie.repository.ScreeningRepository
import woowacourse.movie.view.activities.home.fragments.screeninglist.uistates.ScreeningListUIState
import woowacourse.movie.view.activities.home.fragments.screeninglist.uistates.TheaterUIState
import woowacourse.movie.view.activities.home.fragments.screeninglist.uistates.TheatersUIState
import woowacourse.movie.view.activities.screeningdetail.ScreeningDetailActivity

class ScreeningListFragment : Fragment(), ScreeningListContract.View {

    private val binding: FragmentScreeningListBinding by lazy {
        FragmentScreeningListBinding.inflate(layoutInflater)
    }

    private val presenter: ScreeningListContract.Presenter =
        ScreeningListPresenter(this, ScreeningRepository)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadScreenings()
    }

    override fun setScreeningList(screeningListUIState: ScreeningListUIState) {
        val screeningListView = binding.screeningListView
        screeningListView.adapter =
            ScreeningListAdapter(screeningListUIState.screenings) { screeningId ->
                presenter.onReserveNow(screeningId)
            }
    }

    override fun showTheaters(theaters: TheatersUIState) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView =
            layoutInflater.inflate(R.layout.bottom_sheet_theater, binding.root as ViewGroup, false)
        theaters.theaters.forEach { theater ->
            val theaterItem =
                makeTheaterItem(theaters.screeningId, theater, bottomSheetView as ViewGroup)
            bottomSheetView.addView(theaterItem)
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun makeTheaterItem(
        screeningId: Long,
        theater: TheaterUIState,
        root: ViewGroup
    ): View {
        val itemBinding = ItemTheaterBinding.inflate(layoutInflater, root, false)
        itemBinding.tvTheaterName.text = theater.theaterName
        itemBinding.tvScreeningTimeCount.text =
            getString(R.string.screening_time_count).format(theater.screeningTimeCount)
        itemBinding.root.setOnClickListener {
            ScreeningDetailActivity.startActivity(requireContext(), screeningId, theater.theaterId)
        }
        return itemBinding.root
    }
}
