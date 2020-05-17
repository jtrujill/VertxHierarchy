package com.github.jtrujill.hierarchy.core

import com.github.jtrujill.hierarchy.app.UserWorshiper
import com.github.jtrujill.hierarchy.web.services.HierarchyService
import com.github.jtrujill.hierarchy.web.services.HierarchyServiceImpl
import com.google.inject.AbstractModule
import com.google.inject.BindingAnnotation
import com.google.inject.Provides

@BindingAnnotation
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class WebPort

class MainModule(private val args: Array<String>) : AbstractModule() {
    override fun configure() {
        bindConstant().annotatedWith(WebPort::class.java).to(8080)
    }

    @Provides
    fun provideUserService(userWorshiper: UserWorshiper): HierarchyService {
        return HierarchyServiceImpl(userWorshiper)
    }
}