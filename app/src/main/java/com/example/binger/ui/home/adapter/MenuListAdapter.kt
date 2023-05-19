package com.example.binger.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.LayoutInflaterCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.binger.R
import com.example.binger.databinding.FragmentMenuBinding
import com.example.binger.ui.home.Menus
import com.example.binger.ui.home.ViewModel.menuViewModel
import com.example.binger.ui.home.menu


class MenuListAdapter(
    var menuList: List<Menus?>?,
    val clickListener: MenuListAdapter.MenuListClickListener,
    var viewModel: menuViewModel
) : RecyclerView.Adapter<MenuListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuListAdapter.MyViewHolder {
        val binding = FragmentMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.menu_list, parent, false)

        return MyViewHolder(view, binding)
    }

    fun updateMenuList(updatedMenuList: ArrayList<Menus>) {
        viewModel.menus = updatedMenuList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MenuListAdapter.MyViewHolder, position: Int) {
        holder.bind(viewModel.menus?.get(position)!!, position)
    }

    override fun getItemCount(): Int {
        return viewModel.menus?.size ?: 0
    }

    inner class MyViewHolder(view: View, binding: FragmentMenuBinding) :
        RecyclerView.ViewHolder(view) {
        var thumbImage: ImageView = view.findViewById(R.id.thumbImage)
        val menuName: TextView = view.findViewById(R.id.menuName)
        val menuPrice: TextView = view.findViewById(R.id.menuPrice)
        val addToCartButton: TextView = view.findViewById(R.id.addToCartButton)
        val addMoreLayout: LinearLayout = view.findViewById(R.id.addMoreLayout)
        val imageMinus: ImageView = view.findViewById(R.id.imageMinus)
        val imageAddOne: ImageView = view.findViewById(R.id.imageAddOne)
        val tvCount: TextView = view.findViewById(R.id.tvCount)
        val binding = binding

        fun bind(menu: Menus, position: Int) {
            menuName.text = menu.name
            menuPrice.text = "Price : RM ${menu.price}"

            if (menu.totalInCart >= 1) {
                addToCartButton.visibility = View.GONE
                addMoreLayout.visibility = View.VISIBLE
                tvCount.text = menu.totalInCart.toString()
            }

            addToCartButton.setOnClickListener {
                menu.totalInCart++
                clickListener.addToCartClickListener(menu, binding)
                addMoreLayout.visibility = View.VISIBLE
                addToCartButton.visibility = View.GONE
                tvCount.text = menu.totalInCart.toString()
            }

            imageMinus.setOnClickListener {
                if (menu.totalInCart > 0) {
                    menu.totalInCart--
                    tvCount.text = menu.totalInCart.toString()
                } else {
                    addMoreLayout.visibility = View.GONE
                    addToCartButton.visibility = View.VISIBLE
                }
            }

            imageAddOne.setOnClickListener {
                if (menu.totalInCart < 10) {
                    menu.totalInCart++
                    tvCount.text = menu.totalInCart.toString()
                }
            }

            Glide.with(thumbImage).load(menu.url).into(thumbImage)
        }
    }

    interface MenuListClickListener {
        fun addToCartClickListener(menu: Menus, binding: FragmentMenuBinding)
        /*fun updateCartClickListener(menu: Menus,binding: ActivityRestaurantMenuBinding)*/
        fun removeFromCartClickListener(menu: Menus, binding: FragmentMenuBinding)
    }
}