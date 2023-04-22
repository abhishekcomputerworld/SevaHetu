package com.ritualkart.sevahetu.ui.maplocation;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mapmyindia.sdk.plugins.places.placepicker.model.PlacePickerOptions;
import com.mmi.services.api.Place;
import com.ritualkart.sevahetu.R;

public class CurrentPlaceSelectionBottomSheet extends CoordinatorLayout {
    private BottomSheetBehavior a;
    private CoordinatorLayout b;
    private TextView c;
    private TextView d;
    private ProgressBar e;

    public CurrentPlaceSelectionBottomSheet(Context var1) {
        this(var1, (AttributeSet)null);
    }

    public CurrentPlaceSelectionBottomSheet(Context var1, AttributeSet var2) {
        this(var1, var2, -1);
    }

    public CurrentPlaceSelectionBottomSheet(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        this.a(var1);
    }

    private void a(Context var1) {
        (this.a = BottomSheetBehavior.from((this.b = (CoordinatorLayout)CoordinatorLayout.inflate(var1, R.layout.mapmyindia_view_bottom_sheet_container, this)).findViewById(R.id.root_bottom_sheet))).setHideable(true);
        this.a.setState(5);
        this.c();
    }

    private void c() {
        this.c = (TextView)this.findViewById(R.id.text_view_place_name);
        this.d = (TextView)this.findViewById(R.id.text_view_place_address);
        this.e = (ProgressBar)this.findViewById(R.id.progress_bar_place);
    }

    private void d() {
        this.a.setPeekHeight(this.b.findViewById(R.id.root_bottom_sheet).getHeight());
        boolean var1;
        boolean var10000 = var1 = this.b();
        this.a.setHideable(var1);
        BottomSheetBehavior var2 = this.a;
        byte var3;
        if (var10000) {
            var3 = 5;
        } else {
            var3 = 4;
        }

        var2.setState(var3);
    }

    public void setPickerOptions(PlacePickerOptions var1) {
        this.c.setTextColor(var1.placeNameTextColor());
        this.d.setTextColor(var1.addressTextColor());
    }

    public void setPlaceDetails(@Nullable Place var1) {
        if (!this.b()) {
            this.d();
        }

        if (var1 == null) {
            this.c.setText("");
            this.d.setText("");
            this.e.setVisibility(0);
        } else {
            this.e.setVisibility(4);
            TextView var2 = this.c;
            String var3;
            if (var1.getPoi() != null && !var1.getPoi().equalsIgnoreCase("")) {
                var3 = var1.getPoi();
            } else {
                var3 = "Dropped Pin";
                var3 = var1.getLocality();
            }

            var2.setText(var3);
            this.d.setText(var1.getFormattedAddress());
        }
    }

    public void a() {
        this.d();
    }

    public boolean b() {
        return this.a.getState() != 5;
    }
}
