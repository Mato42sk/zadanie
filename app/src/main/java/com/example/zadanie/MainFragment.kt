package com.example.zadanie

import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.*
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.fragment_main.*
import com.gorisse.thomas.sceneform.scene.await as await

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var arFragment: ArFragment
    private val arSceneView get() = arFragment.arSceneView
    private val scene get() = arSceneView.scene
    private var model: Renderable? = null
    private var anchorNode: AnchorNode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arFragment = (childFragmentManager.findFragmentById(R.id.arFragment) as ArFragment).apply {
            setOnViewCreatedListener { arSceneView ->
                arSceneView.setFrameRateFactor(SceneView.FrameRate.FULL)
            }
            setOnTapArPlaneListener(::onTapPlane)
        }

        remove_btn.setOnClickListener{
            val anchorNodes = arFragment.transformationSystem.selectedNode

            if (anchorNodes != null){
                anchorNodes.apply {
                    parent = null
                    renderable = null
                }
                anchorNode = null
            }else{
                Toast.makeText(context, "Select an object to remove", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launchWhenCreated {
            loadModels()
        }
    }

    private suspend fun loadModels() {

        val katana = ModelRenderable.builder()
            .setSource(context, Uri.parse("models/katana.glb"))
            .setIsFilamentGltf(true).await()

        val banana = ModelRenderable.builder()
            .setSource(context, Uri.parse("models/banana.glb"))
            .setIsFilamentGltf(true).await()

        val car = ModelRenderable.builder()
            .setSource(context, Uri.parse("models/car.glb"))
            .setIsFilamentGltf(true).await()

        val crate = ModelRenderable.builder()
            .setSource(context, Uri.parse("models/crate.glb"))
            .setIsFilamentGltf(true).await()

        val phone = ModelRenderable.builder()
            .setSource(context, Uri.parse("models/phone.glb"))
            .setIsFilamentGltf(true).await()

        val table = ModelRenderable.builder()
            .setSource(context, Uri.parse("models/table.glb"))
            .setIsFilamentGltf(true).await()

        Katana.setOnClickListener { model = katana }
        Banana.setOnClickListener { model = banana }
        Car.setOnClickListener { model = car }
        Crate.setOnClickListener { model = crate }
        Phone.setOnClickListener { model = phone }
        Table.setOnClickListener { model = table }
    }

    private fun onTapPlane(hitResult: HitResult, plane: Plane, motionEvent: MotionEvent) {
        if (model == null) {
            Toast.makeText(context, "Select a model", Toast.LENGTH_SHORT).show()
            return
        }

        anchorNode = AnchorNode(hitResult.createAnchor()).apply {

            addChild(TransformableNode(arFragment.transformationSystem).apply {
                renderable = model
                renderableInstance.animate(true).start()

                addChild(Node().apply {
                    localPosition = Vector3(0.0f, 1f, 0.0f)
                    localScale = Vector3(0.7f, 0.7f, 0.7f)
                })
            })
        }
        scene.addChild(anchorNode)
    }
}