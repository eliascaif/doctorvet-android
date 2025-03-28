package com.xionce.doctorvetServices.utilities;

/**
 * Created by Elias on 18/8/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.ColorRes;

import com.google.android.material.textfield.TextInputLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;

import com.xionce.doctorvetServices.BuildConfig;
import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

//import static com.facebook.FacebookSdk.getApplicationContext;

public class HelperClass {

    private static final String TAG = "HelperClass";

    public static final int REQUEST_TAKE_THUMB = 1;
    public static final int REQUEST_OPEN_GALLERY = 2;
    public static final int REQUEST_READ_BARCODE = 3;
    public static final int REQUEST_SEARCH = 4;
    public static final int REQUEST_UPDATE = 5;
    public static final int REQUEST_TAKE_VIDEO = 8;
    public static final int REQUEST_CALL = 10;
    public static final int REQUEST_RECORD_AUDIO = 11;
    public static final int REQUEST_READ_QR = 13;
    public static final int REQUEST_CREATE = 14;
    public static final int REQUEST_FINISH = 15;
    public static final int REQUEST_TAKE_IMAGE = 16;
    public static final int REQUEST_GET = 17;
    public static final int REQUEST_READ_KEY_PEM = 18;
//    public static final int REQUEST_READ_PEM = 19;

    public static final String INTENT_IMAGE_URL = "imageUrl";
    public static final String INTENT_VIDEO_URL = "videoUrl";

    public static final String INTENT_EXTRA_BARCODE = "barcode";
    public static final String INTENT_EXTRA_REQUEST_CODE = "requestCode";
    //public static final String INTENT_EXTRA_QR = "qr";

    public static final String INTENT_EXTRA_INIT = "init";

    public interface AdapterOnClickHandler {
        void onClick(Object data, View view, int pos);
    }
    public interface AdapterOnOkClickHandler {
        void onOkClick(Object data, View view, int pos);
    }
    public interface AdapterOnCancelClickHandler {
        void onCancelClick(Object data, View view, int pos);
    }
    public interface AdapterOnLongClickHandler {
        void onLongClick(Object data, View view, int pos);
    }
    public interface AdapterOnSelectClickHandler {
        void onSelectClick(Object data, View view, int pos/*, boolean ampliado*/);
    }
    public interface AdapterOnRemoveItemHandler {
        void onRemoveItem(Object data, View view, int pos);
    }
    public interface AdapterRefresh {
        void refreshAdapter();
    }
    public interface RecyclerOnPaginationHandler {
        void onPagination();
    }

    public static void setEnabledViews(ViewGroup container, boolean enabled) {
        for (int i = 0; i < container.getChildCount(); i++) {
            final View child = container.getChildAt(i);
            if (child instanceof ViewGroup) {
                setEnabledViews((ViewGroup) child, enabled);
            } else {
                if (child != null) {
                    child.setEnabled(enabled);
                    //child.setFocusable(enabled);
                    //child.setFocusableInTouchMode(enabled);
                }
            }
        }
    }
    public static String generateGUID() {
        //uuid = universal unique | guid = global unique
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
    public static String createAudioFile(Context ctx) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioFileName = "AUDIO_" + timeStamp + ".mp3"; // ".3gp";
        return ctx.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/" + audioFileName;
    }
    public static String createFile(Context ctx, String extension) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "FILE_" + timeStamp + "." + extension;
        return ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;
    }

    public static String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }
    public static Bitmap bitmapFromPath(String path) {
        Bitmap bitmap = null;
        try {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    public static Bitmap stringToBitmap(String img) {
        byte[] imgBytes = Base64.decode(img, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        return bitmap;
    }
    public static String fileToString(File file) {
        byte[] imgBytes = new byte[0];
        try {
            //imgBytes = IOUtils.toByteArray(file);
            //imgBytes = Files.readAllBytes(file.toPath());
            //import java.io.RandomAccessFile;
            RandomAccessFile f = new RandomAccessFile(file, "r");
            imgBytes = new byte[(int)f.length()];
            f.readFully(imgBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }
    public static byte[] fileToByteArray(File file) {
        byte[] imgBytes = new byte[0];
        try {
            //imgBytes = IOUtils.toByteArray(file);
            //imgBytes = Files.readAllBytes(file.toPath());
            //import java.io.RandomAccessFile;
            RandomAccessFile f = new RandomAccessFile(file, "r");
            imgBytes = new byte[(int)f.length()];
            f.readFully(imgBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgBytes;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
    public static Bitmap makeThumbnail(String imagePath, int thumbSize) {
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), thumbSize, thumbSize);
        return thumbImage;
    }
    public static BitmapDrawable bitmapToIcon(Bitmap bitmap) {
        BitmapDrawable icon = new BitmapDrawable(bitmap);
        return icon;
    }
    public static Bitmap compressBitmapToJpg(Bitmap bitmap) {
        Bitmap resized = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.2), (int)(bitmap.getHeight()*0.2), false);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 80, out);
        Bitmap jpgCoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        //Bitmap thumb = Bitmap.createScaledBitmap(resized, 64, 64, false); //120
        return jpgCoded;
    }
    public static Bitmap compressBitmapToJpg_2(Bitmap bitmap) {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap resized = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.3), (int)(bitmap.getHeight()*0.3), false);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 80, out);
        Bitmap jpgCoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()), null, bmpFactoryOptions);
        return jpgCoded;
    }
    public static Bitmap compressBitmapToWebp(Bitmap bitmap) {
        Bitmap resized = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.2), (int)(bitmap.getHeight()*0.2), false);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.WEBP, 80, out);
        Bitmap webp = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        //Bitmap thumb = Bitmap.createScaledBitmap(resized, 64, 64, false); //120
        return webp;
    }
    public static void saveBitmap(String filename, Bitmap bitmap) {
        try (FileOutputStream out = new FileOutputStream(filename)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    //doesnt work
    public static byte[] getByteArrayFromBitmap_Fast(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(byteBuffer);

        return byteBuffer.array();
    }

    public static String compressedBase64ToString(String compressed_string) throws IOException {
        byte[] compressed = Base64.decode(compressed_string, Base64.DEFAULT);
        byte[] decompressed = Compression.decompress(compressed);
        String json = new String(decompressed, StandardCharsets.UTF_8); // for UTF-8 encoding
        return json;
    }
    public static String getMimeTypeFromUri(Uri uri, ContentResolver contentResolver) {
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        String type = contentResolver.getType(uri);
        return type;
    }
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
    public static String getExtensionFromUri(Uri uri, ContentResolver contentResolver) {
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(contentResolver.getType(uri));
        return type;
    }
    public static String getExtensionFromFilename(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
    public static boolean isValidUri(String uri)
    {
        return URLUtil.isValidUrl(uri);
    }
    public static boolean doesObjectContainField(Object object, String fieldName) {
        Class<?> objectClass = object.getClass();
        for (Field field : objectClass.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }
    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }
    public static Dialog getOverlayDialog(Context ctx) {
        Dialog mOverlayDialog = new Dialog(ctx, android.R.style.Theme_Panel); //display an invisible overlay dialog to prevent user interaction and pressing back
        mOverlayDialog.setCancelable(false);
        return mOverlayDialog;
    }
    public static float pixelTodp(Context c, float pixel) {
        float density = c.getResources().getDisplayMetrics().density;
        float dp = pixel / density;
        return dp;
    }
    public static float dpTopixel(Context c, float dp) {
        float density = c.getResources().getDisplayMetrics().density;
        float pixel = dp * density;
        return pixel;
    }
    public static boolean validateEmpty(TextInputLayout txtInput) {
        String userInput = txtInput.getEditText().getText().toString().trim();
        if (userInput.isEmpty()) {
            txtInput.setError(txtInput.getContext().getString(R.string.error_campo_empty));
            txtInput.getEditText().requestFocus();

            handleScrollView(txtInput);

            return false;
        } else {
            txtInput.setError(null);
            return true;
        }
    }
    public static boolean validateExistence(TextInputLayout txtInput, boolean null_empty_pass) { //, /*HashMap*/ ArrayAdapter<String> list) {
        if (null_empty_pass && txtInput.getEditText().getText().toString().isEmpty())
            return true;

        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) txtInput.getEditText();
        String input = autoCompleteTextView.getText().toString().trim();

        ArrayAdapter<Object> list = (ArrayAdapter<Object>)autoCompleteTextView.getAdapter();
        int listCount = list.getCount();
        for (int i = 0; i < listCount; i++) {
            String value = list.getItem(i).toString();
            if (value.equals(input)) {
                txtInput.setError(null);
                return true;
            }
        }

        txtInput.setError(txtInput.getContext().getString(R.string.error_corrige_campo_no_existe));
        txtInput.requestFocus();

        handleScrollView(txtInput);

        return false;
    }
    public static boolean validateDate(TextInputLayout txtInput, boolean null_empty_pass) {
        String input = txtInput.getEditText().getText().toString().trim();

        if (input.isEmpty() && null_empty_pass) {
            txtInput.setError(null);
            return true;
        }

        Context ctx = txtInput.getContext();

        if (!isValidDate(input, getShortDatePattern(ctx))) {
            txtInput.setError(ctx.getString(R.string.error_corrige_campo));
            txtInput.getEditText().requestFocus();

            handleScrollView(txtInput);

            return false;
        } else {
            txtInput.setError(null);
            return true;
        }
    }
    public static boolean validateHour(TextInputLayout txtInput, boolean null_empty_pass) {
        String input = txtInput.getEditText().getText().toString().trim();

        if (input.isEmpty() && null_empty_pass) {
            txtInput.setError(null);
            return true;
        }

        Context ctx = txtInput.getContext();

        if (!isValidHour(input, ctx)) {
            txtInput.setError(ctx.getString(R.string.error_corrige_campo));
            txtInput.getEditText().requestFocus();

            handleScrollView(txtInput);

            return false;
        } else {
            txtInput.setError(null);
            return true;
        }
    }
    public static boolean validateEmail(TextInputLayout txtInput, boolean null_empty_pass) {
        String email = txtInput.getEditText().getText().toString().trim();

        if (email.isEmpty() && null_empty_pass)
            return true;

        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (!pattern.matcher(email).matches()) {
            txtInput.setError(txtInput.getContext().getString(R.string.error_corrige_campo));
            txtInput.getEditText().requestFocus();

            handleScrollView(txtInput);

            return false;
        } else {
            txtInput.setError(null);
            return true;
        }
    }
    public static boolean validateEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (!pattern.matcher(email).matches()) {
            return false;
        } else {
            return true;
        }
    }
    public static boolean validateNumber(TextInputLayout txtInput, boolean null_empty_pass) {
        String input = txtInput.getEditText().getText().toString().trim();

        if (input.isEmpty() && null_empty_pass) {
            txtInput.setError(null);
            return true;
        }

        if (!NumberUtils.isCreatable(input)) {
            txtInput.setError(txtInput.getContext().getString(R.string.error_corrige_campo));
            txtInput.getEditText().requestFocus();

            handleScrollView(txtInput);

            return false;
        } else {
            txtInput.setError(null);
            return true;
        }
    }
    public static boolean validateNumberNotZero(TextInputLayout txtInput, boolean null_empty_pass) {
        String input = txtInput.getEditText().getText().toString().trim();

        if (input.isEmpty() && null_empty_pass) {
            txtInput.setError(null);
            return true;
        }

        if (NumberUtils.isCreatable(input)) {
            BigDecimal val = new BigDecimal(input);
            BigDecimal valZero = new BigDecimal(0);
            if (!val.equals(valZero)) {
                txtInput.setError(null);
                return true;
            }
        }

        txtInput.setError(txtInput.getContext().getString(R.string.error_corrige_campo));
        txtInput.getEditText().requestFocus();

        handleScrollView(txtInput);

        return false;
    }
    public static void handleScrollView(TextInputLayout txtInput) {
        ScrollView possible_scroll_view = getScrollView(txtInput);
        if (possible_scroll_view != null) {
            Integer top = txtInput.getTop();
//            if (top.equals(0))
//                top = possible_scroll_view.getBottom();

            possible_scroll_view.smoothScrollTo(0, top);
        }
    }
    private static ScrollView getScrollView(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            if (parent instanceof ScrollView)
                return (ScrollView) parent;
            else
                return getScrollView((View) parent);
        }

        return null;
    }

    //DateTime -------------------------------------------------------------------------------------
    public static String getShortDatePattern(Context ctx) {
        DateFormat shortDateFormat = android.text.format.DateFormat.getDateFormat(ctx);

        String pattern = "";
        if (shortDateFormat instanceof SimpleDateFormat) {
            pattern = ((SimpleDateFormat) shortDateFormat).toPattern();
        }

        return pattern;
    }
    public static String getShortTimePattern(Context ctx) {
        DateFormat shortDateFormat = android.text.format.DateFormat.getTimeFormat(ctx);

        String pattern = "";
        if (shortDateFormat instanceof SimpleDateFormat) {
            pattern = ((SimpleDateFormat) shortDateFormat).toPattern();
        }

        return pattern;
    }

    public static String getShortDateTimePattern(Context ctx) {
        return getShortDatePattern(ctx) + " " + getShortTimePattern(ctx);
    }
    public static String getLongDatePattern(Context ctx) {
        DateFormat longDateFormat = android.text.format.DateFormat.getLongDateFormat(ctx);

        String pattern = "";
        if (longDateFormat instanceof SimpleDateFormat) {
            pattern = ((SimpleDateFormat) longDateFormat).toPattern();
        }

        return pattern;
    }

    public static Locale getCurrentLocale(Context ctx) {
        return ctx.getResources().getConfiguration().locale;
    }
    public static String getDateInLocaleShort(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(getShortDatePattern(DoctorVetApp.get().getApplicationContext()));
        return timeFormat.format(date);
    }
    public static String getDateTimeInLocaleShort(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(getShortDateTimePattern(DoctorVetApp.get().getApplicationContext()));
        return timeFormat.format(date);
    }

    public static String getDateInLocaleLong(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(getLongDatePattern(DoctorVetApp.get().getApplicationContext()));
        return timeFormat.format(date);
    }
    public static String getMySqlDateFormat(String shortDate, String originalPattern) {
//        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
//        return timeFormat.format(shortDate);
//
//
//
//
//            String mytime="Jan 17, 2012";
        SimpleDateFormat dateFormat = new SimpleDateFormat(originalPattern);

        Date myDate = null;
        try {
            myDate = dateFormat.parse(shortDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
        return timeFormat.format(myDate);

//
//            System.out.println(finalDate);
//            */
//

    }
    public static boolean isValidDate(String dateToValidate, String dateFormat) {

        if (dateToValidate == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        try {

            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);
            //System.out.println(date);

        } catch (ParseException e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean isValidDate(String dateToValidate, Context ctx) {
        return isValidDate(dateToValidate, getShortDatePattern(ctx));
    }
    public static boolean isValidHour(String dateToValidate, Context ctx) {
        return isValidDate(dateToValidate, getShortTimePattern(ctx));
    }
    public static String getCurrentDate_inStr() {
        return android.text.format.DateFormat.format("yyyy-MM-dd", Calendar.getInstance().getTime()).toString();
    }
    public static String getShortTime_inStr(Date date, Context ctx) {
        return android.text.format.DateFormat.format(getShortTimePattern(ctx), date).toString();
    }
    public static String getLongTime_inStr(Date date, Context ctx) {
        return android.text.format.DateFormat.format(getShortDateTimePattern(ctx), date).toString();
    }
    public static String getShortDate_inStr(Date date, Context ctx) {
        return android.text.format.DateFormat.format(getShortDatePattern(ctx), date).toString();
    }
    public static String getCurrentDatetime_inStr() {
        return android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", Calendar.getInstance().getTime()).toString();
    }
    public static String getCurrentTime_inStr() {
        return android.text.format.DateFormat.format("HH:mm:ss", Calendar.getInstance().getTime()).toString();
    }

    public static Date getDate(String input) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public static Date getDatetime(String input) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getShortDate(String input, Context ctx) {
        //String input = "Thu Jun 18 20:56:02 EDT 2009";
        SimpleDateFormat parser = new SimpleDateFormat(getShortDatePattern(ctx));
        Date date = null;
        try {
            date = parser.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        //String formattedDate = formatter.format(date);
        return date;
    }
    public static Date getShortDateTime(String input, Context ctx) {
        //String input = "Thu Jun 18 20:56:02 EDT 2009";
        SimpleDateFormat parser = new SimpleDateFormat(getShortDateTimePattern(ctx));
        Date date = null;
        try {
            date = parser.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        //String formattedDate = formatter.format(date);
        return date;
    }
    public static Date getShortTime(String input, Context ctx) {
        SimpleDateFormat parser = new SimpleDateFormat(getShortTimePattern(ctx));
        Date date = null;
        try {
            date = parser.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getShortTimeFromMySqlString(String input) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = sdf.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String getDateTimeForMySQL(Date date) {
        return android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", date).toString();
    }
    public static String getDateForMySQL(Date date) {
        return android.text.format.DateFormat.format("yyyy-MM-dd", date).toString();
    }
    public static String getTimeForMySQL(Date date) {
        return android.text.format.DateFormat.format("HH:mm", date).toString();
    }

    public static String getDateTimeInLocale(Date date, Context ctx) {
        String localePattern = HelperClass.getShortDateTimePattern(ctx);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(localePattern);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }
    public static String getTimeInLocale(Date date, Context ctx) {
        String localePattern = HelperClass.getShortTimePattern(ctx);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(localePattern);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }
    public static String getDateInLocale(Date date, Context ctx) {
        String localePattern = HelperClass.getShortDatePattern(ctx);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(localePattern);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    public static Date sumar_dias(Date date, Integer dias) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, dias);
        Date dt_proxima = cal.getTime();
        return dt_proxima;
    }
    public static String calculateAge(Date birthDate) {
        int years = 0;
        int months = 0;
        int days = 0;

        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());

        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);

        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;

        //Get difference between months
        months = currMonth - birthMonth;

        //if month difference is in negative then reduce years by one
        //and calculate the number of months.
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }

        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        }
        else
        {
            days = 0;
            if (months == 12)
            {
                years++;
                months = 0;
            }
        }
        //Create new Age object
        return years + " " + R.string.anos + ", " + months + " " + R.string.meses + ", " + days + " " + R.string.dias + ".";   //new Age(days, months, years);
    }
    public static boolean isLong(String number) {
        try {
            Long.parseLong(number);
            return true;
        } catch (NumberFormatException var1) {
            return false;
        }
    }
    public static boolean isDecimal(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException var1) {
            return false;
        }
    }
    public static long getSizeInKB(String path) {
        File f = new File(path);
        long size = f.length();
        return size;
    }
    public static long getSizeInKBFromUri(Uri uri, ContentResolver contentResolver) {
        InputStream in;
        long size = 0;
        int chunk = 0;

        try {
            in =  contentResolver.openInputStream(uri);

            byte[] buffer = new byte[1024];
            while((chunk = in.read(buffer)) != -1){
                size += chunk;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return size / 1024;
    }
    public static String getPath(Context ctx, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = ctx.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
    public static boolean isMyServiceRunning2(Class<?> serviceClass, Activity activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static long getVideoDurationInMiliseconds(Context ctx, Uri uriToFile) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //Uri.fromFile(new File(inputPath))
        retriever.setDataSource(ctx, uriToFile);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMiliSeconds = Long.parseLong(time);
        retriever.release();
        return timeInMiliSeconds;
    }
    public static boolean deleteFile(String inputPath) {
        File f = new File(inputPath);
        return f.delete();
    }
    public static String objectToString(Serializable obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(
                    new Base64OutputStream(baos, Base64.NO_PADDING
                            | Base64.NO_WRAP));
            oos.writeObject(obj);
            oos.close();
            return baos.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Object stringToObject(String str) {
        try {
            return new ObjectInputStream(new Base64InputStream(
                    new ByteArrayInputStream(str.getBytes()), Base64.NO_PADDING
                    | Base64.NO_WRAP)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void setSystemBarColor(Activity act, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(color));
        }
    }
    public int getBackgroundColor(View view) {
        int color = Color.TRANSPARENT;
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();
        return color;
    }
    public static void sendWhatsAppMessage(Activity context, String phone, String message) throws NoWhatsAppException {
        if (!PhoneNumberUtils.isGlobalPhoneNumber(phone))
            throw new EnterPhoneException();

        //Funciona perfecto a numero desconocido o a conocido
        PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try
        {
            String url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + URLEncoder.encode(message, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                context.startActivity(i);
            } else {
                throw new NoWhatsAppException();
            }
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            throw e;
        }
    }
    public static class NoWhatsAppException extends RuntimeException {
    }
    public static void makePhoneCall(Activity activity, String phone_number) throws EnterPhoneException {
        if (phone_number == null || phone_number.trim().length() == 0)
            throw new EnterPhoneException();

        if (!PhoneNumberUtils.isGlobalPhoneNumber(phone_number))
            throw new EnterPhoneException();

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            return;
        }

        String dial = "tel:" + phone_number.trim();
        activity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
    }
    public static class EnterPhoneException extends RuntimeException {
    }
    public static void sendSMS(Activity activity, String phone_number) throws EnterPhoneException {
        if (phone_number == null || phone_number.trim().length() == 0)
            throw new EnterPhoneException();

        if (!PhoneNumberUtils.isGlobalPhoneNumber(phone_number))
            throw new EnterPhoneException();

        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone_number.trim(), null)));
    }
    public static void sendEmail(Activity activity, String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        //intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        //intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");
        activity.startActivity(Intent.createChooser(intent, "Send Email"));
    }

    public static void getOKCancelDialog(Context ctx, String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }
    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }
    public static Integer getResourceKeyFromUrl(String photo_url) {
        if (photo_url == null || photo_url.isEmpty())
            return 1;

        int ultimo_guion_bajo = photo_url.lastIndexOf("_") + 1;
        int ultimo_punto = photo_url.lastIndexOf(".");
        int number = Integer.parseInt(photo_url.substring(ultimo_guion_bajo, ultimo_punto));
        return ++number;
    }
    public static JSONObject getJsonObject(String json) {
        //JSONObject obj = null;
        try {
            JSONObject obj = new JSONObject(json);
            return obj;
        } catch (Throwable ex) {
            Log.e("HelperClass", ex.getMessage());
            return null;
        }
    }
    public static JSONArray getJsonArray(String json) {
        //JSONObject obj = null;
        try {
            JSONArray array = new JSONArray(json);
            return array;
        } catch (Throwable ex) {
            Log.e("HelperClass", ex.getMessage());
            return null;
        }
    }
    public static boolean checkPermission(String permission, Context ctx) {
        int currentApiVersion = Build.VERSION.SDK_INT;
        if(currentApiVersion >= Build.VERSION_CODES.M)
            return (ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED);

        return true;
    }
    public static void requestPermission(Activity activity, String permission ,int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    public static String getAndroidDirType(String file_name) {
        int ultimo_punto = file_name.lastIndexOf(".");
        if (ultimo_punto > 1) {
            //int punto_a_final_cadena = file_name.length() - ultimo_punto;
            String extension = file_name.substring(ultimo_punto);
            if (extension.equals(".jpg") || extension.equals(".jpeg"))
                return Environment.DIRECTORY_PICTURES;

            if (extension.equals(".mp4"))
                return Environment.DIRECTORY_MOVIES;

            if (extension.equals(".mp3"))
                return Environment.DIRECTORY_MUSIC;
        }

        return Environment.DIRECTORY_DOWNLOADS;
    }
    public static String getFileNameFromPath(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }
    public static String appendToFileName(String path, String toAppend) {
        Integer dot = path.lastIndexOf(".");
        return path.substring(0, dot) + toAppend + path.substring(dot);
    }
    public static String normalizeString(String input) {
        String normalized = Normalizer.normalize(input.toLowerCase(), Normalizer.Form.NFD);
        String ascii = normalized.replaceAll("[^\\p{ASCII}]", "");
        return ascii;
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static Date getDateWithoutTimeUsingCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
    public static String formatCurrency(BigDecimal number) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        String currency = format.format(number);
        return currency;
    }
    public static BigDecimal getBigDecimalFromCurrency(String formattedCurrency) {
        try {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
            Number number = format.parse(formattedCurrency);
            return new BigDecimal(number.doubleValue());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public static boolean viewPdf(String fileName, Context ctx) {
        Uri uri = getFileUri(ctx, fileName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(ctx.getPackageManager()) != null) {
            ctx.startActivity(intent);
            return true;
        }

        return false;
    }
    public static boolean viewFile(String fileName, Context ctx) {
        //Uri uri = getFileUri(ctx, fileName);
        //Uri uri = Uri.fromFile(new File(fileName));

        File file = new File(fileName);
        Uri uriData;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            uriData = FileProvider.getUriForFile(ctx,BuildConfig.APPLICATION_ID + ".provider", file);
        else
            uriData = Uri.fromFile(file);

        String mimeType = getMimeType(uriData.toString());

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uriData, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

//
//        //Intent intent = new Intent(Intent.ACTION_VIEW);
//        //intent.setDataAndType(uri, "image/*");
//        //intent.setDataAndType(uri, "application/pdf");
//
//        // FLAG_GRANT_READ_URI_PERMISSION is needed on API 24+ so the activity opening the file can read it
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(ctx.getPackageManager()) == null) {
            return false;
        } else {
            ctx.startActivity(intent);
            return true;
        }
    }
    public static Uri getFileUri(Context context, String fileName) {
        File file = getFile(context, fileName);
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
    }
    public static File getFile(Context context, String fileName) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File storageDir = context.getExternalFilesDir(null);
        return new File(storageDir, fileName);
    }

    public static String writeToFile(String data, Context context, String fileName) throws IOException {
        File path = context.getExternalFilesDir(null);
        File file = new File(path, fileName); //"sell.pdf");
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(Base64.decode(data, Base64.DEFAULT));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }

        return file.getPath();
    }
    public static Bitmap cropToSquare(Bitmap bitmap){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }
    public static boolean fileExists(String path) {
        File file = new File(path);
        if (file.exists())
            return true;

        return false;
    }

    public static int getLastFirstVisiblePosition(RecyclerView recyclerView) {
        int lastFirstVisiblePosition = -1;
        if (recyclerView.getAdapter() != null)
            lastFirstVisiblePosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        return lastFirstVisiblePosition;
    }
    public static void setLastFirstVisiblePosition(RecyclerView recyclerView, int lastFirstVisiblePosition) {
        if (lastFirstVisiblePosition != -1)
            ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
    }
    public static void focusLastRow(RecyclerView recyclerView) {
        //get adapter from recycler
        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if (adapter == null)
            return;

        // Obtén la posición de la última fila insertada
        int lastPosition = adapter.getItemCount() - 1;

        // Desplázate suavemente a la última fila insertada
        recyclerView.smoothScrollToPosition(lastPosition);
    }


    public static Bitmap checkRotation(String photoPath, Bitmap bitmap) {
        ExifInterface ei = null;
        int orientation;

        try {
            ei = new ExifInterface(photoPath);
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bitmap rotatedBitmap = null;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
