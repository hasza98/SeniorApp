package android.seniorapp

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.seniorapp.model.studyType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.animation.doOnEnd
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialContainerTransform
import java.io.InputStream

private const val NAME_PARAM = "name"
private const val IMG_PARAM = "imgSource"
private const val DESC_PARAM = "desc"
private const val TYPE_PARAM = "type"
private const val YEAR_PARAM = "year"


class PolaroidFragment : Fragment() {
    private var name: String? = null
    private var imgSource: String? = null
    private var desc: String? = null
    private var type: studyType? = null
    private var year: Int? = null
    private var isFrontShowing: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
        arguments?.let {
            name = it.getString(NAME_PARAM)
            imgSource = it.getString(IMG_PARAM)
            desc = it.getString(DESC_PARAM)
            type = studyType.valueOf(it.getString(TYPE_PARAM).toString())
            year = it.getInt(YEAR_PARAM)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_polaroid, container, false)
        val frontView = v.findViewById<LinearLayoutCompat>(R.id.largeCardFront)
        val backView = v.findViewById<LinearLayoutCompat>(R.id.largeCardBack)
        val nameTv = frontView.findViewById<AppCompatTextView>(R.id.largePersonName)
        val image = frontView.findViewById<AppCompatImageView>(R.id.largePersonImage)
        val descTv = backView.findViewById<AppCompatTextView>(R.id.largePersonDesc)
        val fab = frontView.findViewById<FloatingActionButton>(R.id.largePersonFab)
        val yearTv = frontView.findViewById<AppCompatTextView>(R.id.largePersonYear)
        nameTv.text = name
        if(imgSource.equals("null"))
            image.setImageBitmap(getBitmapFromAssets("Images/placeholder.jpg"))
        else
            image.setImageBitmap(getBitmapFromAssets("Images/"+imgSource))
        descTv.text = desc
        when(type) {
            studyType.electrical -> {
                fab.backgroundTintList = ColorStateList.valueOf(context?.getColor(R.color.Orange)!!)
                fab.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_bolt_48))
            }
            studyType.bprof -> {
                fab.backgroundTintList = ColorStateList.valueOf(context?.getColor(R.color.Turq)!!)
                fab.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_engineering_48))
            }
            studyType.informatics -> {
                fab.backgroundTintList = ColorStateList.valueOf(context?.getColor(R.color.darkBlue)!!)
                fab.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_computer_48w))
            }
        }
        yearTv.text = year.toString() + ". year"
        backView.visibility = View.GONE
        v.setOnClickListener {
            if(isFrontShowing)
            {
                flipCard(v.context, backView, frontView)
                isFrontShowing = false
            }
            else
            {
                flipCard(v.context, frontView, backView)
                isFrontShowing = true
            }
        }
        return v
    }

    companion object {
        @JvmStatic
        fun newInstance(name: String, imgSource: String, desc: String, type: studyType, year: Int) =
            PolaroidFragment().apply {
                arguments = Bundle().apply {
                    putString(NAME_PARAM, name)
                    putString(IMG_PARAM, imgSource)
                    putString(DESC_PARAM, desc)
                    putString(TYPE_PARAM, type.toString())
                    putInt(YEAR_PARAM, year)
                }
            }
    }

    private fun flipCard(context: Context, visibleView: View, inVisibleView: View) {
        try {
            visibleView.visibility = View.VISIBLE
            val scale = context.resources.displayMetrics.density
            val cameraDist = 24000 * scale
            visibleView.cameraDistance = cameraDist
            inVisibleView.cameraDistance = cameraDist
            val flipOutAnimatorSet =
                AnimatorInflater.loadAnimator(
                    context,
                    R.animator.flip_out
                ) as AnimatorSet
            flipOutAnimatorSet.setTarget(inVisibleView)
            val flipInAnimatorSet =
                AnimatorInflater.loadAnimator(
                    context,
                    R.animator.flip_in
                ) as AnimatorSet
            flipInAnimatorSet.setTarget(visibleView)
            flipOutAnimatorSet.start()
            flipInAnimatorSet.start()
            flipInAnimatorSet.doOnEnd {
                inVisibleView.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBitmapFromAssets(fileName: String?): Bitmap? {
        val assetManager = this.activity?.assets
        val istr: InputStream = assetManager!!.open(fileName!!)
        val bitmap = BitmapFactory.decodeStream(istr)
        istr.close()
        return bitmap
    }
}