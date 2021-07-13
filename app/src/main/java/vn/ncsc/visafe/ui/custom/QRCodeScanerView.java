package vn.ncsc.visafe.ui.custom;

import android.content.Context;
import android.util.AttributeSet;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScanerView extends ZXingScannerView {
    public QRCodeScanerView(Context context) {
        super(context);
    }

    public QRCodeScanerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected IViewFinder createViewFinderView(Context context) {
        return new ViewFinderCustom(context);
    }
}
