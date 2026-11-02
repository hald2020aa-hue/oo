package eu.kanade.tachiyomi.ui.home

import android.animation.ValueAnimator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import eu.kanade.presentation.util.Screen
import eu.kanade.presentation.util.isTabletUi
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.browse.BrowseTab
import eu.kanade.tachiyomi.ui.download.DownloadQueueScreen
import eu.kanade.tachiyomi.ui.history.HistoryTab
import eu.kanade.tachiyomi.ui.library.LibraryTab
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import eu.kanade.tachiyomi.ui.more.MoreTab
import eu.kanade.tachiyomi.ui.updates.UpdatesTab
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import mihon.app.di.appGraph
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.pluralStringResource

object HomeScreen : Screen() {

    private val librarySearchEvent = Channel<String>()
    private val openTabEvent = Channel<Tab>()
    private val showBottomNavEvent = Channel<Boolean>()

    @Suppress("ConstPropertyName")
    private const val TabFadeDuration = 220

    @Suppress("ConstPropertyName")
    private const val TabNavigatorKey = "HomeTabs"

    private val TABS = listOf(
        LibraryTab,
        UpdatesTab,
        HistoryTab,
        BrowseTab,
        MoreTab,
    )

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val animationsEnabled = remember { ValueAnimator.areAnimatorsEnabled() }
        TabNavigator(
            tab = LibraryTab,
            key = TabNavigatorKey,
        ) { tabNavigator ->
            // Provide usable navigator to content screen
            CompositionLocalProvider(LocalNavigator provides navigator) {
                val tabletUi = isTabletUi()
                val navigationSuiteType = if (tabletUi) {
                    NavigationSuiteType.NavigationRail
                } else {
                    NavigationSuiteType.NavigationBar
                }
                val navigationSuiteState = rememberNavigationSuiteScaffoldState()
                LaunchedEffect(navigationSuiteState, tabletUi) {
                    if (tabletUi) navigationSuiteState.show()
                    showBottomNavEvent.receiveAsFlow().collectLatest { show ->
                        if (tabletUi || show) {
                            navigationSuiteState.show()
                        } else {
                            navigationSuiteState.hide()
                        }
                    }
                }

                NavigationSuiteScaffold(
                    navigationSuiteType = navigationSuiteType,
                    state = navigationSuiteState,
                    navigationSuiteColors = NavigationSuiteDefaults.colors(
                        navigationRailContainerColor = MaterialTheme.colorScheme
                            .surfaceColorAtElevation(3.dp),
                    ),
                    navigationItemVerticalArrangement = Arrangement.Center,
                    navigationItems = {
                        TABS.fastForEach { NavigationSuiteItem(it, navigationSuiteType) }
                    },
                ) {
                    AnimatedContent(
                        targetState = tabNavigator.current,
                        transitionSpec = {
                            materialFadeThroughIn(
                                initialScale = 0.97f,
                                durationMillis = if (animationsEnabled) TabFadeDuration else 0,
                            ) togetherWith materialFadeThroughOut(durationMillis = if (animationsEnabled) TabFadeDuration else 0)
                        },
                        label = "tabContent",
                    ) {
                        tabNavigator.saveableState(key = "currentTab", it) {
                            it.Content()
                        }
                    }
                }
            }

            val goToLibraryTab = { tabNavigator.current = LibraryTab }

            BackHandler(enabled = tabNavigator.current != LibraryTab, onBack = goToLibraryTab)

            LaunchedEffect(Unit) {
                launch {
                    librarySearchEvent.receiveAsFlow().collectLatest {
                        goToLibraryTab()
                        LibraryTab.search(it)
                    }
                }
                launch {
                    openTabEvent.receiveAsFlow().collectLatest {
                        tabNavigator.current = when (it) {
                            is Tab.Library -> LibraryTab
                            Tab.Updates -> UpdatesTab
                            Tab.History -> HistoryTab
                            is Tab.Browse -> {
                                if (it.toExtensions) {
                                    BrowseTab.showExtension()
                                }
                                BrowseTab
                            }
                            is Tab.More -> MoreTab
                        }

                        if (it is Tab.Library && it.mangaIdToOpen != null) {
                            navigator.push(MangaScreen(it.mangaIdToOpen))
                        }
                        if (it is Tab.More && it.toDownloads) {
                            navigator.push(DownloadQueueScreen)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NavigationSuiteItem(
        tab: eu.kanade.presentation.util.Tab,
        navigationSuiteType: NavigationSuiteType,
    ) {
        val tabNavigator = LocalTabNavigator.current
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val selected = tabNavigator.current::class == tab::class
        // Honour Android's "Remove animations" accessibility preference.
        val animationsEnabled = remember { ValueAnimator.areAnimatorsEnabled() }
        val durationMillis = if (animationsEnabled) 180 else 0
        val iconScale by animateFloatAsState(
            targetValue = if (selected) 1.1f else 1f,
            animationSpec = tween(durationMillis = durationMillis),
            label = "neonTabIconScale",
        )
        val glowAlpha by animateFloatAsState(
            targetValue = if (selected) 0.15f else 0f,
            animationSpec = tween(durationMillis = durationMillis),
            label = "neonTabHaloAlpha",
        )
        val iconTint by animateColorAsState(
            targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            animationSpec = tween(durationMillis = durationMillis),
            label = "neonTabIconTint",
        )
        val iconRes = when (tab) {
            LibraryTab -> R.drawable.neon_tab_books
            UpdatesTab -> R.drawable.neon_tab_bell
            HistoryTab -> R.drawable.neon_tab_history
            BrowseTab -> R.drawable.neon_tab_compass
            else -> R.drawable.neon_tab_settings
        }
        NavigationSuiteItem(
            navigationSuiteType = navigationSuiteType,
            selected = selected,
            onClick = {
                if (!selected) {
                    tabNavigator.current = tab
                } else {
                    scope.launch { tab.onReselect(navigator) }
                }
            },
            icon = {
                // Crisp Tabler vectors, tinted by the active Neon Cyber theme.
                // The halo is a low-opacity Compose shape, not a costly blur.
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha),
                                shape = CircleShape,
                            ),
                    )
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = tab.options.title,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp).scale(iconScale),
                    )
                }
            },
            label = {
                Text(
                    text = tab.options.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            badge = tabBadge(tab),
        )
    }

    @Composable
    private fun tabBadge(tab: eu.kanade.presentation.util.Tab): (@Composable () -> Unit)? {
        val context = LocalContext.current
        val count by produceState(initialValue = 0, tab) {
            val graph = context.appGraph
            when (tab) {
                is UpdatesTab -> {
                    combine(
                        graph.libraryPreferences.newShowUpdatesCount.changes(),
                        graph.libraryPreferences.newUpdatesCount.changes(),
                    ) { show, count ->
                        if (show) count else 0
                    }
                        .collectLatest { value = it }
                }

                is BrowseTab -> {
                    graph.sourcePreferences.extensionUpdatesCount.changes()
                        .collectLatest { value = it }
                }

                else -> value = 0
            }
        }
        if (count <= 0) return null
        return {
            Badge {
                val desc = when (tab) {
                    is UpdatesTab -> pluralStringResource(
                        MR.plurals.notification_chapters_generic,
                        count = count,
                        count,
                    )

                    is BrowseTab -> pluralStringResource(
                        MR.plurals.update_check_notification_ext_updates,
                        count = count,
                        count,
                    )

                    else -> null
                }
                Text(
                    text = count.toString(),
                    modifier = Modifier.semantics {
                        if (desc != null) contentDescription = desc
                    },
                )
            }
        }
    }

    suspend fun search(query: String) {
        librarySearchEvent.send(query)
    }

    suspend fun openTab(tab: Tab) {
        openTabEvent.send(tab)
    }

    suspend fun showBottomNav(show: Boolean) {
        showBottomNavEvent.send(show)
    }

    sealed interface Tab {
        data class Library(val mangaIdToOpen: Long? = null) : Tab
        data object Updates : Tab
        data object History : Tab
        data class Browse(val toExtensions: Boolean = false) : Tab
        data class More(val toDownloads: Boolean) : Tab
    }
}
