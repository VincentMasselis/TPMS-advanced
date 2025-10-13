package com.masselis.tpmsadvanced.core.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.getkeepsafe.relinker.ReLinker
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import io.requery.android.database.sqlite.SQLiteDatabase.LIBRARY_NAME

public class SQLiteOpenHelperUseCase internal constructor(context: Context) {

    private val loadNativeLibrary by lazy {
        // Replace the default System.loadLibrary(LIBRARY_NAME) which is less reliable than
        // ReLinker.loadLibrary()
        ReLinker.loadLibrary(context, LIBRARY_NAME)
    }

    public val factory: SupportSQLiteOpenHelper.Factory
        get() {
            loadNativeLibrary
            return RequerySQLiteOpenHelperFactory()
        }
}
