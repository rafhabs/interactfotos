package com.rafhabs.interactfotos

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var image_uri: Uri? = null

    // bloco de valores obrigatorios nos metodos
    companion object {
        private val PERMISSION_CODE_IMAGE_PICK = 1000
        private val IMAGE_PICK_CODE_IMAGE_PICK = 1001


        private val PERMISSION_CODE_CAMERA        = 2000
        private val CAMERA_OPEN_CODE_IMAGE_CAMERA = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pick_button.setOnClickListener{
            // conferindo se a versão do usuário é superior a configuração do APP
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // confere se a permissão está negada
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //faz a requisição da permissão
                    val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE_IMAGE_PICK)
                } else {
                    pickImageFromGallery()
                }
            } else {
                pickImageFromGallery()
            }
        }

        open_camera_button.setOnClickListener {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                    //faz a requisição da permissão para camera e para escrever
                    val permissioncw = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permissioncw, PERMISSION_CODE_CAMERA)

                } else {
                    openCamera()
                }
            } else
            {
                openCamera()
            }

        }

    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {

            // codigo a ser executado quando a requisicao vir de imagens
            PERMISSION_CODE_IMAGE_PICK -> {
                // conferindo a permissão autorizada
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else
                {
                    // envia alerta na tela para o usuário
                    Toast.makeText(this,"Permissão negada",Toast.LENGTH_SHORT).show()
                }
            }

            // codigo a ser executado quando a requisicao vir da camera
            PERMISSION_CODE_CAMERA -> {
                // conferindo a permissão autorizada
                if ((grantResults.size > 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                } else
                {
                    // envia alerta na tela para o usuário
                    Toast.makeText(this,"Permissão negada",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Nova foto")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descrição da nova foto")

        // pegando a imagem e passa os values como parametros
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        // abre a intenção de abrir uma camera
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // colocando parametros extras na camera
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)

        // dando resposta para a tela
        startActivityForResult(cameraIntent, CAMERA_OPEN_CODE_IMAGE_CAMERA)

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        // pegando apenas imagens
        intent.type = "image/*"
        // iniciando a activity
        startActivityForResult(intent, IMAGE_PICK_CODE_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // confere se a ação deu certo e se o codigo da requisição veio do album
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE_IMAGE_PICK) {
            // passa o resultado para a tela
            image_view.setImageURI(data?.data)
        }

        // confere se a ação deu certo e se o codigo da requisição veio da camera
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_OPEN_CODE_IMAGE_CAMERA) {
            // passa o resultado para a tela
            image_view.setImageURI(image_uri)
        }

    }


}