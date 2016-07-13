# Pull Down View. WIP

Not finished yet

Usage. Somewhere around your app. Having a reference of an activity (I will change this but lets start from scratch)

```Java
new PullDownView.Builder(activityReference)
		.header(aViewThatWillBeShownAsHeader)
		.content(aViewThatWillBeShownAsContent)
		.listener(toReceiveCallbacksFromImportantEvents)
		.build().showHeader(theTimeThatWillBeShown);

//If you hold the instance you can also do manually
mPullDownView.showContent();
mPullDownView.hideContent();
mPullDownView.hideHeader();
mPullDownView.showHeader(); //Same as the one with the time, when time is 0
```
