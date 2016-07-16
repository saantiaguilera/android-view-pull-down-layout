# Pull Down View. WIP

Not finished yet

Usage. Somewhere around your app. Having a reference of an activity (Else we dont have where to inject this notification)

```Java
new PullDownView.Builder(activityReference)
		.header(aViewThatWillBeShownAsHeader)
		.content(aViewThatWillBeShownAsContent)
		.onViewVisibilityChanged(toReceiveCallbacksFromImportantEvents)
		.onContentVisibilityChanged(toReceiveCallbacksFromImportantEvents)
		.build().showHeader(theTimeThatWillBeShown);

//If you hold the instance you can also do manually
mPullDownView.showContent();
mPullDownView.hideContent();
mPullDownView.hideHeader();
mPullDownView.showHeader(); //Same as the one with the time, when time is 0
```
