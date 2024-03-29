package net.osmand.plus.activities.actions;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import net.osmand.AndroidUtils;
import net.osmand.plus.ApplicationMode;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.OsmandSettings;
import net.osmand.plus.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AppModeDialog {

	public static View prepareAppModeView(Activity a, final Set<ApplicationMode> selected, boolean showDefault,
	                                      ViewGroup parent, final boolean singleSelection, boolean useListBg, boolean useMapTheme, final View.OnClickListener onClickListener) {
		OsmandApplication app = (OsmandApplication) a.getApplication();
		OsmandSettings settings = app.getSettings();
		final List<ApplicationMode> values = new ArrayList<>(ApplicationMode.values(app));
		if (!showDefault) {
			values.remove(ApplicationMode.DEFAULT);
		}
		if (showDefault || (settings.getApplicationMode() != ApplicationMode.DEFAULT && !singleSelection)) {
			selected.add(settings.getApplicationMode());
		}
		return prepareAppModeView(a, values, selected, parent, singleSelection, useListBg, useMapTheme, onClickListener);
	}

	//special method for drawer menu
	//needed because if there's more than 4 items  - the don't fit in drawer
	public static View prepareAppModeDrawerView(Activity a, final Set<ApplicationMode> selected,
	                                            boolean useMapTheme, final View.OnClickListener onClickListener) {
		OsmandApplication app = (OsmandApplication) a.getApplication();
		OsmandSettings settings = app.getSettings();
		final List<ApplicationMode> values = new ArrayList<>(ApplicationMode.values(app));
		selected.add(settings.getApplicationMode());
		return prepareAppModeView(a, values, selected, null, true, true, useMapTheme, onClickListener);
	}

	public static View prepareAppModeView(Activity a, final List<ApplicationMode> values, final Set<ApplicationMode> selected,
	                                      ViewGroup parent, final boolean singleSelection, boolean useListBg, boolean useMapTheme, final View.OnClickListener onClickListener) {
		boolean nightMode = isNightMode(((OsmandApplication) a.getApplication()), useMapTheme);

		return prepareAppModeView(a, values, selected, parent, singleSelection, useListBg, useMapTheme, onClickListener, nightMode);
	}

	public static View prepareAppModeView(Activity a, final List<ApplicationMode> values, final Set<ApplicationMode> selected,
	                                      ViewGroup parent, final boolean singleSelection, boolean useListBg, boolean useMapTheme, final View.OnClickListener onClickListener, boolean nightMode) {
		View ll = a.getLayoutInflater().inflate(R.layout.mode_toggles, parent);
		if (useListBg) {
			AndroidUtils.setListItemBackground(a, ll, nightMode);
		} else {
			ll.setBackgroundColor(ContextCompat.getColor(a, nightMode ? R.color.route_info_bg_dark : R.color.route_info_bg_light));
		}
		final View[] buttons = new View[values.size()];
		int k = 0;
		for (ApplicationMode ma : values) {
			buttons[k++] = createToggle(a.getLayoutInflater(), (OsmandApplication) a.getApplication(), R.layout.mode_view, (LinearLayout) ll.findViewById(R.id.app_modes_content), ma, useMapTheme);
		}
		for (int i = 0; i < buttons.length; i++) {
			updateButtonState((OsmandApplication) a.getApplication(), values, selected, onClickListener, buttons, i,
					singleSelection, useMapTheme, nightMode);
		}
		return ll;
	}


	public static void updateButtonState(final OsmandApplication ctx, final List<ApplicationMode> visible,
	                                     final Set<ApplicationMode> selected, final View.OnClickListener onClickListener, final View[] buttons,
	                                     int i, final boolean singleChoice, final boolean useMapTheme, final boolean nightMode) {
		if (buttons[i] != null) {
			View tb = buttons[i];
			final ApplicationMode mode = visible.get(i);
			final boolean checked = selected.contains(mode);
			ImageView iv = (ImageView) tb.findViewById(R.id.app_mode_icon);
			if (checked) {
				iv.setImageDrawable(ctx.getUIUtilities().getIcon(mode.getIconRes(ctx), nightMode ? R.color.active_buttons_and_links_dark : R.color.route_info_checked_mode_icon_color_light));
				iv.setContentDescription(String.format("%s %s", mode.toHumanString(ctx), ctx.getString(R.string.item_checked)));
				tb.findViewById(R.id.selection).setVisibility(View.VISIBLE);
			} else {
				if (useMapTheme) {
					iv.setImageDrawable(ctx.getUIUtilities().getIcon(mode.getIconRes(ctx), R.color.description_font_and_bottom_sheet_icons));
					iv.setBackgroundResource(AndroidUtils.resolveAttribute(ctx, android.R.attr.selectableItemBackground));
				} else {
					iv.setImageDrawable(ctx.getUIUtilities().getThemedIcon(mode.getIconRes(ctx)));
				}
				iv.setContentDescription(String.format("%s %s", mode.toHumanString(ctx), ctx.getString(R.string.item_unchecked)));
				tb.findViewById(R.id.selection).setVisibility(View.INVISIBLE);
			}
			iv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean isChecked = !checked;
					if (singleChoice) {
						if (isChecked) {
							selected.clear();
							selected.add(mode);
						}
					} else {
						if (isChecked) {
							selected.add(mode);
						} else {
							selected.remove(mode);
						}
					}
					if (onClickListener != null) {
						onClickListener.onClick(null);
					}
					for (int i = 0; i < visible.size(); i++) {
						updateButtonState(ctx, visible, selected, onClickListener, buttons, i, singleChoice, useMapTheme, nightMode);
					}
				}
			});
		}
	}

	public static void updateButtonStateForRoute(final OsmandApplication ctx, final List<ApplicationMode> visible,
	                                             final Set<ApplicationMode> selected, final View.OnClickListener onClickListener, final View[] buttons,
	                                             int i, final boolean singleChoice, final boolean useMapTheme, final boolean nightMode) {
		if (buttons[i] != null) {
			View tb = buttons[i];
			final ApplicationMode mode = visible.get(i);
			final boolean checked = selected.contains(mode);
			ImageView iv = (ImageView) tb.findViewById(R.id.app_mode_icon);
			View selection = tb.findViewById(R.id.selection);

			if (checked) {
				Drawable drawable = ctx.getUIUtilities().getIcon(mode.getIconRes(ctx), nightMode ? R.color.active_buttons_and_links_dark : R.color.active_buttons_and_links_light);
				iv.setImageDrawable(drawable);
				iv.setContentDescription(String.format("%s %s", mode.toHumanString(ctx), ctx.getString(R.string.item_checked)));

				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
					AndroidUtils.setBackground(ctx, iv, nightMode, R.drawable.btn_border_light, R.drawable.btn_border_dark);
					AndroidUtils.setBackground(ctx, selection, nightMode, R.drawable.ripple_light, R.drawable.ripple_dark);
				} else {
					AndroidUtils.setBackground(ctx, selection, nightMode, R.drawable.btn_border_trans_light, R.drawable.btn_border_trans_dark);
				}
			} else {
				if (useMapTheme) {
					Drawable drawable = ctx.getUIUtilities().getIcon(mode.getIconRes(ctx), nightMode ? R.color.route_info_control_icon_color_dark : R.color.route_info_control_icon_color_light);
					if (Build.VERSION.SDK_INT >= 21) {
						Drawable active = ctx.getUIUtilities().getIcon(mode.getIconRes(ctx), nightMode ? R.color.active_buttons_and_links_dark : R.color.active_buttons_and_links_light);
						drawable = AndroidUtils.createPressedStateListDrawable(drawable, active);
					}
					iv.setImageDrawable(drawable);
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
						AndroidUtils.setBackground(ctx, iv, nightMode, R.drawable.btn_border_pressed_light, R.drawable.btn_border_pressed_dark);
						AndroidUtils.setBackground(ctx, selection, nightMode, R.drawable.ripple_light, R.drawable.ripple_dark);
					} else {
						AndroidUtils.setBackground(ctx, selection, nightMode, R.drawable.btn_border_pressed_trans_light, R.drawable.btn_border_pressed_trans_dark);
					}
				} else {
					iv.setImageDrawable(ctx.getUIUtilities().getThemedIcon(mode.getIconRes(ctx)));
				}
				iv.setContentDescription(String.format("%s %s", mode.toHumanString(ctx), ctx.getString(R.string.item_unchecked)));
			}
			tb.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean isChecked = !checked;
					if (singleChoice) {
						if (isChecked) {
							selected.clear();
							selected.add(mode);
						}
					} else {
						if (isChecked) {
							selected.add(mode);
						} else {
							selected.remove(mode);
						}
					}
					if (onClickListener != null) {
						onClickListener.onClick(null);
					}
					for (int i = 0; i < visible.size(); i++) {
						updateButtonStateForRoute(ctx, visible, selected, onClickListener, buttons, i, singleChoice, useMapTheme, nightMode);
					}
				}
			});
		}
	}

	static public View createToggle(LayoutInflater layoutInflater, OsmandApplication ctx, @LayoutRes int layoutId, LinearLayout layout, ApplicationMode mode, boolean useMapTheme) {
		int metricsX = (int) ctx.getResources().getDimension(R.dimen.route_info_modes_height);
		int metricsY = (int) ctx.getResources().getDimension(R.dimen.route_info_modes_height);
		View tb = layoutInflater.inflate(layoutId, null);
		ImageView iv = (ImageView) tb.findViewById(R.id.app_mode_icon);
		iv.setImageDrawable(ctx.getUIUtilities().getIcon(mode.getIconRes(ctx), isNightMode(ctx, useMapTheme) ? R.color.active_buttons_and_links_dark : R.color.route_info_checked_mode_icon_color_light));
		iv.setContentDescription(mode.toHumanString(ctx));
//		tb.setCompoundDrawablesWithIntrinsicBounds(null, ctx.getIconsCache().getIcon(mode.getIconId(), R.color.app_mode_icon_color), null, null);
		LayoutParams lp = new LinearLayout.LayoutParams(metricsX, metricsY);
//		lp.setMargins(left, 0, 0, 0);
		layout.addView(tb, lp);
		return tb;
	}

	private static boolean isNightMode(OsmandApplication ctx, boolean useMapTheme) {
		boolean nightMode;
		if (useMapTheme) {
			nightMode = ctx.getDaynightHelper().isNightModeForMapControls();
		} else {
			nightMode = !ctx.getSettings().isLightContent();
		}
		return nightMode;
	}
}
