
package com.gubo.liftlab

import android.os.*
import android.view.*
import android.widget.*
import android.support.v7.app.*
import android.support.v7.widget.*

class MainActivity : AppCompatActivity(),DBListener
{
    private val postAdapter = PostAdapter()
    private val rtdb = RTDB()

    private var recyclerview:RecyclerView? = null
    private var edit:EditText? =  null

    override fun onCreate( savedInstanceState:Bundle? ) {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.main )

        recyclerview = findViewById( R.id.recyclerview ) as RecyclerView
        edit = findViewById( R.id.edit ) as EditText

        recyclerview?.layoutManager = LinearLayoutManager( baseContext )
        recyclerview?.setHasFixedSize( true )
        recyclerview?.adapter = postAdapter
    }

    override fun onResume() {
        super.onResume()
        rtdb.on( applicationContext,this )
    }

    fun onPost( view: View) {
        val text = edit?.text
        if ( (text != null) && !text.isEmpty() ) {
            rtdb.push( "${text}" )
        }
    }

    override fun onUpdate( posts:List<Post> ) {
        postAdapter.posts.clear()
        postAdapter.posts.addAll( posts )
        recyclerview?.adapter?.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        rtdb.off( applicationContext )
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    class PostHolder( itemView:View ) : RecyclerView.ViewHolder( itemView )
    {
        val textView:TextView?

        init {
            textView = itemView.findViewById( R.id.textview ) as TextView
        }

        fun bind( post:Post ) {
            textView?.text = post?.text
        }
    }

    class PostAdapter : RecyclerView.Adapter<PostHolder>()
    {
        val posts : MutableList<Post> = ArrayList();

        override fun getItemCount(): Int {
            return posts.size
        }

        override fun onCreateViewHolder( parent:ViewGroup?,viewType:Int ): PostHolder {
            return PostHolder( LayoutInflater.from( parent?.context ).inflate( R.layout.post,parent,false ) )
        }

        override fun onBindViewHolder( holder:PostHolder?,position:Int ) {
            holder?.bind( posts[ position ] )
        }
    }
}
