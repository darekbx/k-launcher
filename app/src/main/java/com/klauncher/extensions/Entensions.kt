package com.klauncher.extensions

import android.app.Activity
import android.app.Fragment
import android.support.annotation.IdRes
import android.view.View
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.threadToAndroid(): Single<T> {
    return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Maybe<T>.threadToAndroid(): Maybe<T> {
    return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.notNull(view: Any): Maybe<T> {
    return this
            .filter { _ -> view != null }
}


fun <T> Observable<T>.threadToAndroid(): Observable<T> {
    return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.notNull(view: Any): Observable<T> {
    return this
            .filter { _ -> view != null }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun <T : View> Activity.bind(@IdRes idRes: Int): Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return unsafeLazy { findViewById<T>(idRes) }
}

fun <T : View> Fragment.bind(@IdRes idRes: Int): Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return unsafeLazy { view.findViewById<T>(idRes) }
}

fun <T : View> View.bind(@IdRes idRes: Int): Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return unsafeLazy { findViewById<T>(idRes) }
}

private fun <T> unsafeLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

fun Int.format(digits: Int) = java.lang.String.format("%0${digits}d", this)
fun Double.format(digits: Int) = java.lang.String.format("%0${digits}f", this)