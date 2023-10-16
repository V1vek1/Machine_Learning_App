package com.example.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var buttonCamera= findViewById<Button>(R.id.btnCamera)

        buttonCamera.setOnClickListener {

            //Intent ka matlab hota h "Irada" matlab ki yeh yha per yeh bol rha h ki
            //Media se, matlab app se Camera app Open krakar Photo click kare

            val intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            //Aab hum Yha if-else Condition lga rhe h, Kyuki ho sakta h ki Yeh uper wala Intent sahi se
            //work na kare to uss conditon me hame kya karna h

            if (intent.resolveActivity(packageManager) != null)  //Agar null return nahi karta h, matab sab thik chal rha h
            {
                // "startActivityForResult" ko hum ek new app/Screen per lekar jane keliye use karte h, per aab isko bund kar
                // diya gya h iske liye, per yeh abhi bhi kaam karta h

                startActivityForResult(intent, 123)
            }
            else
            {
                Toast.makeText(this, "Oops Somthing went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // "onActivityResult" hum use kar rhe h taki hum camera app se information lene ke baad phir se, Phele wale Screen per Data lekar
    // aa sake

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Yha maine if-else isliye use kiya h taki yeh pta chale ki camera aap se data sahi aaya to h per wo kis format me
        // photo lega yeh pta hona chaiye

        if(requestCode==123 && resultCode== RESULT_OK)
        {
            // "bitmap" ek format h jiske help se hum Image ko Bitmap m convert krake usko use karenge
            // "?" Question mark lgane ka matlab h ki agar data galat bhi aata h to, Hame Error Show nhi ho

               val extras= data?.extras
               val bitmap= extras?.get("data") as? Bitmap

            if (bitmap != null) //Agar bitmap null hoga to hum niche wala "fun" yani function ko call nahi karenge
            {
                detectFace(bitmap)
            }
        }
    }

    private fun detectFace(bitmap: Bitmap)
    {
        // Yeh niche wala code, High Accuracy photo ke details lene ke liye use hota h, iske baare me or acche se janne
        // keliye hum android ke Machine learning wale official page per jakar dekh sakte h

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(options)

        // Yha hum Bitmap me jo photo aaya h like selfie/ Camera Photo wo yha image me aa jaye

        val image = InputImage.fromBitmap(bitmap, 0)

            //Yeh Niche wala code Image ko Process karne ke liye use hota h

            val result = detector.process(image)  // Yeh code Result btayega ki, hamara photo sahi se detect ho gya h
            .addOnSuccessListener { faces ->

                //Yeh niche wala code se hum clicked Photo me person ka Information le rhe h ki uska smile Percentage, Left or Right
                // Eyes Opening Percentage kitna h

                var resulText=" "       // Yha maine string bnaya h
                var i=1                 // maine Logon ki Counting 1 se start kiya h
                for (face in faces)     //Yeh code ka matlab h ki, photo me jitne bhi log h unka ek ek karkje details btaye
                {
                    //Yha maine String ko Values Provide kiya h

                    resulText= "Face Number : $i" +
                            "\n Smile : ${face.smilingProbability?.times(100)}%" +
                            "\n Left Eye Open : ${face.leftEyeOpenProbability?.times(100)}%" +
                            "\n Right Eye Open : ${face.rightEyeOpenProbability?.times(100)}%"
                    i++
                }



                //Agar Photo me koi face nahi h to yeh Output me aayega As a Toast

                if (faces.isEmpty())
                {
                   Toast.makeText(this, "No Any Faces Detected", Toast.LENGTH_SHORT).show()
                }

                else       //Agar Photo me Face Detect hota h to "resulText" wale string me jobhi h wo Return ya Output dedo
                {
                    Toast.makeText(this, resulText, Toast.LENGTH_LONG).show()
                }

            }


                // iss code ka matlab h ki, hamara photo sahi se detect NAHI huaa h

            .addOnFailureListener { e ->

                Toast.makeText(this, "Something Wrong", Toast.LENGTH_SHORT).show()
            }

    }

}