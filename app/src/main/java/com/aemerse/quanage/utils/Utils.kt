package com.aemerse.quanage.utils

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import com.aemerse.quanage.R
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.math.sqrt

var toast: Toast? = null

const val sharedPrefFile = "qwhirlSharedPreferences"
val permissionsRequest: Int = 931

val permissionsToGive = arrayOf(
  Manifest.permission.INTERNET,
  Manifest.permission.READ_EXTERNAL_STORAGE,
  Manifest.permission.WRITE_EXTERNAL_STORAGE
)
fun getRealPathFromURI(contentURI: Uri, context: Context): String? {
  val result: String?
  val cursor: Cursor? = context.contentResolver.query(contentURI, null, null, null, null)
  if (cursor == null) {
    result = contentURI.path
  } else {
    cursor.moveToFirst()
    val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
    result = cursor.getString(idx)
    cursor.close()
  }
  return result
}
internal fun Activity.toast(message: CharSequence) {
  toast?.cancel()
  toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
      .apply { show() }
}

typealias PrefEditor = SharedPreferences.Editor

internal inline fun SharedPreferences.commit(crossinline exec: PrefEditor.() -> Unit) {
  val editor = this.edit()
  editor.exec()
  editor.apply()
}

fun askForPermissions(activity: Activity, permissions: Array<String>, requestCode: Int): Boolean {
  val permissionsToRequest: MutableList<String> = ArrayList()
  for (permission in permissions) {
    if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
      permissionsToRequest.add(permission)
    }
  }
  if (permissionsToRequest.isEmpty()) {
    return false
  }
  if (permissionsToRequest.isNotEmpty()) {
    ActivityCompat.requestPermissions(activity, permissionsToRequest.toTypedArray(), requestCode)
  }
  return true
}

fun getDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
  return sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2).toDouble()).toFloat()
}

fun convertDpToPixel(dp: Float, context: Context): Float {
  val resources = context.resources
  val metrics = resources.displayMetrics
  return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun convertPixelsToDp(px: Float, context: Context): Float {
  val resources = context.resources
  val metrics = resources.displayMetrics
  return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun showSnackbar(parent: View?, message: String?, context: Context) {
  val spannableString = SpannableStringBuilder(message)
  spannableString.setSpan(ForegroundColorSpan(Color.WHITE), 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
  val snackbar = Snackbar.make(parent!!, spannableString, Snackbar.LENGTH_LONG)
  snackbar.view.setBackgroundColor(context.resources.getColor(R.color.blue))
  snackbar.show()
}

fun showLongToast(@StringRes textResId: Int, context: Context) {
  showToast(textResId, Toast.LENGTH_LONG, context)
}

private fun showToast(@StringRes textResId: Int, duration: Int, context: Context) {
  Toast.makeText(context, textResId, duration).show()
}
fun copyResultsToClipboard(text: String?, snackbarDisplay: SnackbarDisplay, context: Context) {
  val clipboard = context.getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
  val clip = ClipData.newPlainText(context.getString(R.string.generated_numbers), text)
  clipboard.setPrimaryClip(clip)
  snackbarDisplay.showSnackbar(context.getString(R.string.results_copied_to_clipboard))
}

fun copyHistoryRecordToClipboard(text: String?, context: Context) {
  val clipboard = context.getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
  val clip = ClipData.newPlainText(context.getString(R.string.record), text)
  clipboard.setPrimaryClip(clip)
  showLongToast(R.string.record_copied, context)
}

fun stripHtml(html: String?): String {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
  } else {
    Html.fromHtml(html).toString()
  }
}

interface SnackbarDisplay {
  fun showSnackbar(message: String?)
}

fun hideKeyboard(activity: Activity) {
  val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
  // Find the currently focused view, so we can grab the correct window token from it.
  var view = activity.currentFocus
  // If no view currently has focus, create a new one, just so we can grab a window token from it
  if (view == null) {
    view = View(activity)
  }
  inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Animates a results text view to indicate that the app is doing something.
 * This makes the case where the app generates the same thing twice less awkward.
 */
fun animateResults(resultsText: TextView, newText: CharSequence?, animLength: Int) {
  if (resultsText.animation == null || resultsText.animation.hasEnded()) {
    val animX = ObjectAnimator.ofFloat(resultsText, "scaleX", 0.75f)
    val animY = ObjectAnimator.ofFloat(resultsText, "scaleY", 0.75f)
    val fadeOut = ObjectAnimator.ofFloat(resultsText, "alpha", 0.0f)
    val shrink = AnimatorSet()
    shrink.playTogether(animX, animY, fadeOut)
    shrink.duration = animLength.toLong()
    shrink.interpolator = AccelerateInterpolator()
    shrink.addListener(object : Animator.AnimatorListener {
      override fun onAnimationStart(animation: Animator) {}
      override fun onAnimationEnd(animation: Animator) {
        resultsText.text = newText
        val animX = ObjectAnimator.ofFloat(resultsText, "scaleX", 1.0f)
        val animY = ObjectAnimator.ofFloat(resultsText, "scaleY", 1.0f)
        val fadeIn = ObjectAnimator.ofFloat(resultsText, "alpha", 1.0f)
        val grow = AnimatorSet()
        grow.playTogether(animX, animY, fadeIn)
        grow.duration = animLength.toLong()
        grow.interpolator = AnticipateOvershootInterpolator()
        grow.start()
      }

      override fun onAnimationCancel(animation: Animator) {}
      override fun onAnimationRepeat(animation: Animator) {}
    })
    shrink.start()
  }
}

fun setCheckedImmediately(checkableView: CompoundButton, checked: Boolean) {
  checkableView.isChecked = checked
  checkableView.jumpDrawablesToCurrentState()
}
fun getNumbers(min: Int, max: Int, quantity: Int, noDupes: Boolean, excludedNums: List<Int>?): List<Int> {
  val numbers: MutableList<Int> = ArrayList()
  val excludedNumsSet: MutableSet<Int> = HashSet(excludedNums)
  var numAdded = 0
  while (numAdded < quantity) {
    val attempt = generateNumInRange(min, max)
    if (!excludedNumsSet.contains(attempt)) {
      numbers.add(attempt)
      if (noDupes) {
        excludedNumsSet.add(attempt)
      }
      numAdded++
    }
  }
  return numbers
}

private fun generateNumInRange(min: Int, max: Int): Int {
  return if (min >= 0 && max >= 0) {
    generateNumInPosRange(min, max)
  } else if (min <= 0 && max <= 0) {
    val posNum = generateNumInPosRange(max * -1, min * -1)
    posNum * -1
  } else {
    val deficit = min * -1
    val preShift = generateNumInPosRange(min + deficit, max + deficit)
    preShift - deficit
  }
}

private fun generateNumInPosRange(min: Int, max: Int): Int {
  val random = Random()
  return random.nextInt(max - min + 1) + min
}
fun getResultsString(numbers: List<Int>, showSum: Boolean, numbersPrefix: String?, sumPrefix: String?): String {
  val stringBuilder = StringBuilder()
  stringBuilder.append("<b>")
  stringBuilder.append(numbersPrefix)
  stringBuilder.append("</b>")
  var sum = 0
  for (i in numbers.indices) {
    if (i != 0) {
      stringBuilder.append(", ")
    }
    stringBuilder.append(numbers[i].toString())
    sum += numbers[i]
  }
  if (showSum) {
    stringBuilder.append("<br><br><b>")
    stringBuilder.append(sumPrefix)
    stringBuilder.append("</b>")
    stringBuilder.append(sum.toString())
  }
  return stringBuilder.toString()
}
fun getDiceResults(rolls: List<Int>, rollsPrefix: String?, sumPrefix: String?): String {
  val stringBuilder = StringBuilder()
  stringBuilder.append("<b>")
  stringBuilder.append(rollsPrefix)
  stringBuilder.append("</b>")
  var sum = 0
  for (i in rolls.indices) {
    if (i != 0) {
      stringBuilder.append(", ")
    }
    stringBuilder.append(rolls[i].toString())
    sum += rolls[i]
  }
  stringBuilder.append("<br><br><b>")
  stringBuilder.append(sumPrefix)
  stringBuilder.append("</b>")
  stringBuilder.append(sum.toString())
  return stringBuilder.toString()
}
fun getExcludedList(excludedNums: List<Int>, noExcludedNumbers: String): String {
  val excludedList = StringBuilder()
  if (excludedNums.isEmpty()) {
    return noExcludedNumbers
  }
  for (excludedNum in excludedNums) {
    if (excludedList.isNotEmpty()) {
      excludedList.append(", ")
    }
    excludedList.append(excludedNum.toString())
  }
  return excludedList.toString()
}

fun getCoinResults(flips: List<Int>, context: Context): String {
  val heads = context.getString(R.string.heads)
  val tails = context.getString(R.string.tails)
  val stringBuilder = StringBuilder()
  stringBuilder.append("<b>")
  stringBuilder.append(context.getString(R.string.sides_prefix))
  stringBuilder.append("</b>")
  var numHeads = 0
  var numTails = 0
  for (i in flips.indices) {
    if (i != 0) {
      stringBuilder.append(", ")
    }
    stringBuilder.append(if (flips[i] == 0) heads else tails)
    if (flips[i] == 0) {
      numHeads++
    } else {
      numTails++
    }
  }
  stringBuilder.append("<br><br><b>")
  stringBuilder.append(context.getString(R.string.num_heads_prefix))
  stringBuilder.append("</b>")
  stringBuilder.append(numHeads.toString())
  stringBuilder.append("<br><br><b>")
  stringBuilder.append(context.getString(R.string.num_tails_prefix))
  stringBuilder.append("</b>")
  stringBuilder.append(numTails.toString())
  return stringBuilder.toString()
}
