package edu.ifba.educa_ra.visualizador3D

import android.app.Activity
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import edu.ifba.educa_ra.R

class Visualizador3DActivity: Activity() {

    var superficieVisualizacao: SurfaceView? = null
    var visualizador3D: Visualizador3D = Visualizador3D()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizador_3d)

        superficieVisualizacao = findViewById<View>(R.id.area_visualizacao_3D) as SurfaceView
        visualizador3D.run {
            val b = intent.extras
            if (b != null) {
                loadEntity()
                setSurfaceView(superficieVisualizacao!!)

                b.getString("model")?.let { carregarGlb(it) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        visualizador3D.onResume()
    }

    override fun onPause() {
        super.onPause()
        visualizador3D.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        visualizador3D.onDestroy()
    }
}