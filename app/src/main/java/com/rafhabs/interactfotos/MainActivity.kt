package com.rafhabs.interactfotos

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
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
                    requestPermissions(permission, PERMISSION_CODE)
                } else {
                    pickImageFromGallery()
                }
            } else {
                pickImageFromGallery()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {

            PERMISSION_CODE -> {
                // conferindo a permissão autorizada
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else
                {
                    // envia alerta na tela para o usuário
                    Toast.makeText(this,"Permissão negada",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        // pegando apenas imagens
        intent.type = "image/*"
        // iniciando a activity
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // confere se a ação deu certo e se o codigo da requisição está correto
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            // passa o resultado para a tela
            image_view.setImageURI(data?.data)
        }
    }

    // bloco de valores obrigatorios nos metodos
    companion object {
        private val PERMISSION_CODE = 1000
        private val IMAGE_PICK_CODE = 1001
    }
}