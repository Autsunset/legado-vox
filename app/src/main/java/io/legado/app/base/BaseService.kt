package io.legado.app.base

import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import io.legado.app.R
import io.legado.app.data.local.preferences.LocalPreferencesKeys
import io.legado.app.data.repository.SettingsRepository
import io.legado.app.help.LifecycleHelp
import io.legado.app.help.coroutine.Coroutine
import io.legado.app.lib.permission.Permissions
import io.legado.app.lib.permission.PermissionsCompat
import io.legado.app.utils.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import org.koin.java.KoinJavaComponent.get
import kotlin.coroutines.CoroutineContext

abstract class BaseService : LifecycleService() {

    private val simpleName = this::class.simpleName.toString()
    @Volatile
    private var isForeground = false

    fun <T> execute(
        scope: CoroutineScope = lifecycleScope,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        executeContext: CoroutineContext = Dispatchers.Main,
        semaphore: Semaphore? = null,
        block: suspend CoroutineScope.() -> T
    ) = Coroutine.async(scope, context, start, executeContext, semaphore, block)

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        startForegroundNotification()
        isForeground = true
        LifecycleHelp.onServiceCreate(this)
        val localPreferencesRepository: SettingsRepository = get(SettingsRepository::class.java)
        val checked: Boolean = runBlocking {
            localPreferencesRepository.getPreference(
                LocalPreferencesKeys.PERMISSION_CHECKED, false
            ).first()
        }
        if (!checked) {
            runBlocking {
                localPreferencesRepository.updatePreference(
                    LocalPreferencesKeys.PERMISSION_CHECKED, true
                )
            }
            checkPermission()
        }
    }

    @CallSuper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.d(simpleName) {
            "onStartCommand $intent ${intent?.toUri(0)}"
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @CallSuper
    override fun onTaskRemoved(rootIntent: Intent?) {
        LogUtils.d(simpleName, "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        LifecycleHelp.onServiceDestroy(this)
    }

    @CallSuper
    override fun onTimeout(startId: Int, fgsType: Int) {
        super.onTimeout(startId, fgsType)
        LogUtils.d(simpleName, "onTimeout startId:$startId fgsType:$fgsType")
        stopSelf()
    }

    /**
     * 开启前台服务并发送通知
     */
    open fun startForegroundNotification() {

    }

    /**
     * 检测后台运行权限。通知权限由具体播放/任务入口按场景请求。
     */
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PermissionsCompat.Builder()
                .addPermissions(Permissions.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                .rationale(R.string.ignore_battery_permission_rationale)
                .request()
        }
    }
}
