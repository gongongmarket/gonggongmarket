package com.hansung.c.gonggongmarket.presenter.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.gonggongmarket.R
import com.example.gonggongmarket.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hansung.c.gonggongmarket.presenter.main.fragment.sale.add_update.SaleAddActivity

class MainActivity : AppCompatActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_nav).setupWithNavController(navController)
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_sale) {
                binding.fab.visibility = View.VISIBLE
                binding.appTitle.text = getString(R.string.appbarTitle)
            } else {
                binding.fab.visibility = View.INVISIBLE
                binding.appTitle.text = getString(R.string.app_name)
            }
        }

        binding.fab.setOnClickListener {
            navigateSaleAddView()
        }
    }

    private fun navigateSaleAddView() {
        startActivity(Intent(this, SaleAddActivity::class.java))
    }
}