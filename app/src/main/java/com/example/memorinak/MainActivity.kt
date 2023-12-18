package com.example.memorinak

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

@SuppressLint("StaticFieldLeak")
var dino_1: ImageView? = null
@SuppressLint("StaticFieldLeak")
var dino_2: ImageView? = null

val dino_pictures = arrayOf(R.drawable.dino_memory_card_0,R.drawable.dino_memory_card_1,
    R.drawable.dino_memory_card_2,R.drawable.dino_memory_card_3,R.drawable.dino_memory_card_4,
    R.drawable.dino_memory_card_5,R.drawable.dino_memory_card_6,R.drawable.dino_memory_card_7,
    R.drawable.dino_memory_card_8)

const val default_dino_picture = R.drawable.dino_memory

var found_counter = 0
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(applicationContext)
        layout.orientation = LinearLayout.VERTICAL

        val dino_sprite_indexes = (0..15).toList().toMutableList()

        var num_of_indexes = 16
        val indexes = (0..15).toList().toMutableList()

        while (num_of_indexes > 0){
            val spriteI = (0..7).random()
            dino_sprite_indexes[indexes[0]] = spriteI
            indexes[0] = indexes[num_of_indexes-1]
            num_of_indexes--

            val i = (0 until num_of_indexes).random()
            dino_sprite_indexes[indexes[i]] = spriteI
            indexes[i] = indexes[num_of_indexes-1]
            num_of_indexes--
        }
        val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.weight = 1.toFloat() // единичный вес
        params.height = 500

        val monsterViews = ArrayList<ImageView>()
        for (i in 0..15) {
            monsterViews.add( // вызываем конструктор для создания нового ImageView
                ImageView(applicationContext).apply {
                    val t = dino_sprite_indexes[i]
                    setImageResource(default_dino_picture)
                    layoutParams = params
                    tag = "dino_$t"
                    setOnClickListener(colorListener)
                })
        }

        val rows = Array(4) { LinearLayout(applicationContext) }

        for ((count, view) in monsterViews.withIndex()) {
            val row: Int = count / 4
            rows[row].addView(view)
        }
        for (row in rows) {
            layout.addView(row)
        }
        setContentView(layout)

    }

    @SuppressLint("ShowToast")
    suspend fun setBackgroundWithDelay(v: ImageView) {
        println(v.tag)
        val ind = v.tag.toString().filter { it.isDigit() }.toInt()
        println(ind)
        v.setImageResource(dino_pictures[ind])
        delay(500)
        if (dino_1 == null)
        {
            dino_1 = v
        }
        else if (dino_2 == null)
        {
            dino_2 = v
        }
        if (dino_1 != null && dino_2 != null){
            if (dino_1!!.tag == dino_2!!.tag){
                dino_1!!.visibility = View.INVISIBLE
                dino_2!!.visibility = View.INVISIBLE

                dino_1 = null
                dino_2 = null

                found_counter += 2
                if (found_counter == 16){
                    val toast = Toast.makeText(
                        applicationContext,
                        "Вы победили!",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
            }
            else{
                dino_1!!.setImageResource(default_dino_picture)
                dino_2!!.setImageResource(default_dino_picture)

                dino_1 = null
                dino_2 = null
            }
        }
    }

    // обработчик нажатия на кнопку
    @OptIn(DelicateCoroutinesApi::class)
    val colorListener = View.OnClickListener() {
        // запуск функции в фоновом потоке
        GlobalScope.launch (Dispatchers.Main)
        {
            setBackgroundWithDelay( it as ImageView ) }
    }
}