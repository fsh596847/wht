package android;

/**
 * Created by johnwatson on 4/20/16.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

/**
 * Abstraction class of a BaseAdapter in which you only need to provide the convert()
 * implementation.<br/> Using the provided BaseAdapterHelper, your code is minimalist.
 *
 * @param <T> The type of the items in the list.
 */
public abstract class MyQuickAdapter<T> extends MyBaseQuickAdapter<T, MyBaseAdapterHelper> {

  /**
   * Create a MyQuickAdapter.
   *
   * @param context The context.
   * @param layoutResId The layout resource id of each item.
   */
  public MyQuickAdapter(Context context, int layoutResId) {
    super(context, layoutResId);
  }

  /**
   * Same as MyQuickAdapter#MyQuickAdapter(Context,int) but with some initialization data.
   *
   * @param context The context.
   * @param layoutResId The layout resource id of each item.
   * @param data A new list is created out of this one to avoid mutable list
   */
  public MyQuickAdapter(Context context, int layoutResId, List<T> data) {
    super(context, layoutResId, data);
  }

  protected MyBaseAdapterHelper getAdapterHelper(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      return new MyBaseAdapterHelper(context, parent, layoutResId, position);
    }

    // Retrieve the existing helper and update its position
    MyBaseAdapterHelper existingHelper = (MyBaseAdapterHelper) convertView.getTag();
    existingHelper.position = position;
    return existingHelper;
  }
}