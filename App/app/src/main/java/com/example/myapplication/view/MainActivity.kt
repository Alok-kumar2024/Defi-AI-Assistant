package com.example.myapplication.view

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private lateinit var drawerLayout : DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = binding.main

        val toolbar = binding.ToolBar
        setSupportActionBar(toolbar)

        val navigationView = binding.NvMainActivity

        val toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null)
        {
            supportFragmentManager.beginTransaction()
                .replace(R.id.FrameLayoutMainActivity,DashBoardFragment()).commit()

            navigationView.setCheckedItem(R.id.Dasboard_Main)
        }

        navigationView.setNavigationItemSelectedListener{menuItem->
            when(menuItem.itemId)
            {
                R.id.Dasboard_Main -> {
                    switchFragment(DashBoardFragment())
                }
                R.id.Market_Main -> {
                    switchFragment(MarketFragment())
                }
                R.id.AI_Main -> {
                    switchFragment(Ai_SuggestionsFragment())
                }
                R.id.Settings_Main -> {
                    switchFragment(SettingsFragment())
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    fun switchFragment(fragment:Fragment)
    {
        supportFragmentManager.beginTransaction().replace(R.id.FrameLayoutMainActivity,fragment).commit()
    }


}