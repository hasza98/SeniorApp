package android.seniorapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.seniorapp.adapter.PersonListAdapter
import android.seniorapp.model.Person
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.InputStream
import android.widget.*

class MainActivity : AppCompatActivity() {
    var isBackVisible = false
    val db = DBHelper(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val isFirstRun = getSharedPreferences("SeniorAppPreference", Context.MODE_PRIVATE).getBoolean("isFirstRun", true)
        if(isFirstRun)
        {
            db.populateDatabase()
        }
        val enlargedView = findViewById<RelativeLayout>(R.id.enlargedViewHolder)
        enlargedView.visibility = View.GONE

        val recyclerView = findViewById<View>(R.id.polaroidRV) as RecyclerView
        recyclerView.autoFitColumns(150)
        val adapter = PersonListAdapter(db.getPersons(), object : PersonListAdapter.OnItemClickListener {
            override fun onItemClick(item: Person?) {
                if (item != null) {
                    enlargedView.alpha = 0f
                    enlargedView.visibility = View.VISIBLE
                    enlargedView.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .setListener(null).start()
                    supportFragmentManager.beginTransaction().replace(R.id.container, PolaroidFragment.newInstance(
                        item.name, item.imgSource, item.description, item.type, item.year)).commit()
                }
                enlargedView.setOnClickListener {
                    enlargedView.animate()
                        .alpha(0f)
                        .setDuration(500)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                enlargedView.visibility = View.GONE
                            }
                        }).start()
                    val fragment = supportFragmentManager.findFragmentById(R.id.container)
                    if (fragment != null) {
                        supportFragmentManager.beginTransaction().remove(fragment)
                    }
                }
            }
        })
        recyclerView.adapter = adapter
    }

    private fun RecyclerView.autoFitColumns(columnWidth: Int) {
        val displayMetrics = this.context.resources.displayMetrics
        val noOfColumns = ((displayMetrics.widthPixels / displayMetrics.density) / columnWidth).toInt()
        this.layoutManager = GridLayoutManager(this.context, noOfColumns)
    }
}
