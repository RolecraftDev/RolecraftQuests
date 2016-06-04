/**
 * This package contains Questy's implementations of interfaces such as {@link com.volumetricpixels.questy.QuestManager}, which is currently implemented by both
 * {@link com.volumetricpixels.questy.questy.SimpleQuestManager} and {@link com.volumetricpixels.questy.questy.concurrent.ThreadSafeQuestManager}.
 *
 * Questy as a framework can be used without this package - this package contains implementations of sections of Questy's API, and therefore if you wish to write your own
 * implementations of interfaces such as {@link com.volumetricpixels.questy.QuestManager} and {@link com.volumetricpixels.questy.loading.QuestLoader} it is likely you will want to
 * remove this package.
 */
package com.volumetricpixels.questy.questy;