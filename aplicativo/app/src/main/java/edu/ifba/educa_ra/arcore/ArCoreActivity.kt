@file:Suppress("NAME_SHADOWING")

package edu.ifba.educa_ra.arcore

import android.animation.ValueAnimator
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentOnAttachListener
import edu.ifba.educa_ra.R
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.Sceneform
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.BaseArFragment
import com.google.ar.sceneform.ux.TransformableNode
import edu.ifba.educa_ra.avisar
import edu.ifba.educa_ra.telas.DataHolder
import java.io.File
import java.lang.ref.WeakReference
import java.util.function.Consumer

class VisualizadorARCoreActivity() : AppCompatActivity(), FragmentOnAttachListener, BaseArFragment.OnTapArPlaneListener,
    BaseArFragment.OnSessionConfigurationListener, ArFragment.OnViewCreatedListener {

    private lateinit var areaVisualizacao: ArFragment
    private var modelo: Renderable? = null
    private var rotationAnimator: ValueAnimator? = null
    private var isInteracting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_visualizador_ar)
        supportFragmentManager.addFragmentOnAttachListener(this)

        if (savedInstanceState == null) {
            if (Sceneform.isSupported(this)) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.area_visualizacao_ar, ArFragment::class.java, null)
                    .commit()
            }
        }

        carregarModelo()
    }

    override fun onAttachFragment(fragmentManager: FragmentManager, fragment: Fragment) {
        if (fragment.id == R.id.area_visualizacao_ar) {
            areaVisualizacao = fragment as ArFragment
            areaVisualizacao.setOnSessionConfigurationListener(this)
            areaVisualizacao.setOnViewCreatedListener(this)
            areaVisualizacao.setOnTapArPlaneListener(this)
        }
    }

    private fun glbDisponivel(caminho: String): Boolean {
        val arquivo = File(caminho)

        return arquivo.isFile && arquivo.exists()
    }

    private fun carregarModelo() {
        val disponivel = glbDisponivel("${DataHolder.objetoSelecionado.caminho}/${DataHolder.objetoSelecionado.nome}.glb")

        if (!disponivel) {
            exibirErroCarregamento()
        } else {
            val weakActivity: WeakReference<VisualizadorARCoreActivity> = WeakReference(this)
            ModelRenderable.builder()
                .setSource(
                    this,
                    Uri.parse("file://${DataHolder.objetoSelecionado.caminho}/${DataHolder.objetoSelecionado.nome}.glb")
                )
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(Consumer { model: ModelRenderable ->
                    val activity: VisualizadorARCoreActivity? = weakActivity.get()
                    if (activity != null) {
                        activity.modelo = model
                    }
                })
                .exceptionally {
                    exibirErroCarregamento()
                }
        }
    }

    private fun exibirErroCarregamento(): Void? {
        avisar(this.baseContext, "ocorreu um erro carregando modelo, não será possível exibir o conteúdo")

        return null
    }

    override fun onTapPlane(hitResult: HitResult?, plane: Plane?, motionEvent: MotionEvent?) {
        if (modelo == null) {
            return
        }

        val anchor: Anchor = hitResult!!.createAnchor()
        val anchorNode = AnchorNode(anchor)
        anchorNode.localScale = Vector3(0.1f, 0.1f, 0.1f)
        anchorNode.parent = areaVisualizacao.arSceneView.scene

        val model = TransformableNode(areaVisualizacao.transformationSystem)
        model.parent = anchorNode
        model.setRenderable(modelo)
            .animate(true).start()
        model.select()

        model.rotationController.isEnabled = false

        rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 30000 // Duração da rotação em milissegundos (30 segundos para uma rotação completa)
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                model.localRotation = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), animatedValue)
            }
            start()
        }

        // Parar a rotação ao tocar no objeto e continuar ao soltar
        model.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!isInteracting) {
                        isInteracting = true
                        rotationAnimator?.pause()
                        model.rotationController.isEnabled = true
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isInteracting) {
                        isInteracting = false
                        rotationAnimator?.resume()
                        model.rotationController.isEnabled = false
                    }
                }
            }
            false
        }

    }

    override fun onSessionConfiguration(session: Session?, config: Config?) {
        if (session != null && config != null) {
            if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                config.depthMode = Config.DepthMode.AUTOMATIC
            }
        }
    }

    override fun onViewCreated(arSceneView: ArSceneView?) {
        areaVisualizacao.setOnViewCreatedListener(null)
        arSceneView?.setFrameRateFactor(SceneView.FrameRate.FULL)
    }

}