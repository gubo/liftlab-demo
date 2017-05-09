
package com.gubo.liftlab

import android.content.*

import com.google.firebase.database.*

/**
 * Created by GUBO on 5/9/2017.
 */
class RTDB
{
    private val databaseReference:DatabaseReference
    private val firebaseDatabase: FirebaseDatabase

    private var dbListener:DBListener? = null
    private var on:Boolean = false

    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference( "/pastebin" )
    }

    private val locationValueEventListener = object : ValueEventListener {
        override fun onDataChange( snapshot:DataSnapshot? ) {
            val value = snapshot?.getValue()
            if ( value is HashMap<*,*> ) {
                process( value as HashMap<String,HashMap<String,Any>> )
            }
        }

        override fun onCancelled( error:DatabaseError? ) {
            println( error )
        }
    }

    fun on( context: Context,dbListener:DBListener? = null ) {
        this.dbListener = dbListener
        if ( !on ) {
            databaseReference.addValueEventListener( locationValueEventListener )
            firebaseDatabase.goOnline()
            on = true
        }
    }

    fun push( text:String ) {
        try {
            databaseReference.push().setValue( Post( "1",text ) )
            println( "pushed: " + text )
        } catch ( x:Exception ) {
            x.printStackTrace()
        }
    }

    fun off( context: Context ) {
        databaseReference.removeEventListener( locationValueEventListener )
        firebaseDatabase.goOffline()
        on = false
    }

    private fun process( map : HashMap<String,HashMap<String,Any>> ) {
        val posts : MutableList<Post> = ArrayList();
        for ( (id,fields) in map ) {
            val userId = "${fields.get( "userId" )?:""}"
            val text = "${fields.get( "text" )?:""}"
            posts.add( Post( userId,text ) )
        }
        dbListener?.onUpdate( posts )
    }
}