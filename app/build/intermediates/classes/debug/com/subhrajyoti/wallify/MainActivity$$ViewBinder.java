// Generated code from Butter Knife. Do not modify!
package com.subhrajyoti.wallify;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends com.subhrajyoti.wallify.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558509, "field 'randomFab'");
    target.randomFab = finder.castView(view, 2131558509, "field 'randomFab'");
    view = finder.findRequiredView(source, 2131558510, "field 'setFab'");
    target.setFab = finder.castView(view, 2131558510, "field 'setFab'");
    view = finder.findRequiredView(source, 2131558514, "field 'imageView'");
    target.imageView = finder.castView(view, 2131558514, "field 'imageView'");
    view = finder.findRequiredView(source, 2131558515, "field 'progressBar'");
    target.progressBar = finder.castView(view, 2131558515, "field 'progressBar'");
  }

  @Override public void unbind(T target) {
    target.randomFab = null;
    target.setFab = null;
    target.imageView = null;
    target.progressBar = null;
  }
}
