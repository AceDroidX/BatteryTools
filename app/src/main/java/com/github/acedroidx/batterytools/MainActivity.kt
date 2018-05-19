package com.github.acedroidx.batterytools

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.github.acedroidx.batterytools.fragment.AboutFragment
import com.github.acedroidx.batterytools.fragment.MainFragment
import com.github.acedroidx.batterytools.fragment.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var fragmentNow: Fragment
    lateinit var fragmentNowS: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerLayout:View = navigationView.inflateHeaderView(R.layout.nav_header_main)
        val textWebsite:TextView = headerLayout.findViewById(R.id.textWebsite)
        val appInfo = packageManager
                .getApplicationInfo(packageName,
                        PackageManager.GET_META_DATA)
        val url = appInfo.metaData.getString("Website")
        textWebsite.text = url
        textWebsite.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            textWebsite.text = url
            intent.data = Uri.parse(url)
            startActivity(intent)
        }


        if (savedInstanceState == null) {
            fragmentNow = MainFragment.newInstance()
            fragmentNowS = fragmentNow.javaClass.name
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragmentNow)
                    .commitNow()
        } else {
            if (savedInstanceState.getString("fragmentNow") != null) {
                fragmentNowS = savedInstanceState.getString("fragmentNow")
                fragmentNow = MainViewModel.newFragment(this, fragmentNowS)
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragmentNow)
                        .commitNow()
            } else {
                fragmentNow = MainFragment.newInstance()
                fragmentNowS = fragmentNow.javaClass.name
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragmentNow)
                        .commitNow()
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString("fragmentNow", fragmentNowS)

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_main -> {
                fragmentNow = MainFragment.newInstance()
                fragmentNowS = fragmentNow.javaClass.name
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragmentNow)
                        .commitNow()
            }
            R.id.nav_about -> {
                fragmentNow = AboutFragment.newInstance()
                fragmentNowS = fragmentNow.javaClass.name
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragmentNow)
                        .commitNow()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
