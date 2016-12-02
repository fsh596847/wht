package android;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by johnwatson on 4/20/16.
 */
public class MyBaseAdapterHelper extends BaseAdapterHelper {
  protected MyBaseAdapterHelper(Context context, ViewGroup parent, int layoutId, int position) {
    super(context, parent, layoutId, position);
  }

  public BaseAdapterHelper setCompoundDrawablesWithIntrinsicBounds(int viewId,
      @DrawableRes int left, @DrawableRes int top, @DrawableRes int right,
      @DrawableRes int bottom) {
    TextView view = retrieveView(viewId);
    view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    return this;
  }

  public BaseAdapterHelper setCompoundDrawablePadding(int viewId, int pad) {
    TextView view = retrieveView(viewId);
    view.setCompoundDrawablePadding(pad);
    return this;
  }

  public BaseAdapterHelper setVisible(int viewId, boolean visible) {
    View view = retrieveView(viewId);
    view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    return this;
  }
}