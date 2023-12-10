# KotlinFlows-demo

**Run/Debug Configuration Template**

1. **Edit Configurations...**
2. Click on **Edit Configurations templates...**
3. Select **Kotlin** and set **VM options**:

```shell
-Dkotlinx.coroutines.debug
```

### Android — Interaction between Views and ViewModels

> #### Channel vs. Flow

- [android/channel_vs_flow/ChannelVsFlowActivity.kt](app/src/main/java/org/scarlet/android/channel_vs_flow/ChannelVsFlowActivity.kt)
- [android/channel_vs_flow/ChannelVsFlowViewModel.kt](app/src/main/java/org/scarlet/android/channel_vs_flow/ChannelVsFlowViewModel.kt)

> #### Safe Collecting Flows

- [android/collect/SafeCollectActivity.kt](app/src/main/java/org/scarlet/android/collect/SafeCollectActivity.kt)
- [android/collect/SafeCollectViewModel.kt](app/src/main/java/org/scarlet/android/collect/SafeCollectViewModel.kt)

> #### Password Demo

- [android/password/LoginUiState.kt](app/src/main/java/org/scarlet/android/password/LoginUiState.kt)
- LiveData
    - [android/password/livedata/PasswordActivity.kt](app/src/main/java/org/scarlet/android/password/livedata/PasswordActivity.kt)
    - [android/password/livedata/PasswordViewModel.kt](app/src/main/java/org/scarlet/android/password/livedata/PasswordViewModel.kt)
- Flow
    - [android/password/flow/PasswordActivity.kt](app/src/main/java/org/scarlet/android/password/flow/PasswordActivity.kt)
    - [android/password/flow/PasswordActivity.kt](app/src/main/java/org/scarlet/android/password/flow/PasswordActivity.kt)

> #### Currency Conversion Demo

- [android/currency/CurrencyApi.kt](app/src/main/java/org/scarlet/android/currency/CurrencyApi.kt)
- [android/currency/CurrencyViewModel.kt](app/src/main/java/org/scarlet/android/currency/CurrencyViewModel.kt)
- LiveData
    - [android/currency/livedata/CurrencyActivity.kt](app/src/main/java/org/scarlet/android/currency/livedata/CurrencyActivity.kt)
    - [android/currency/livedata/CurrencyViewModel.kt](app/src/main/java/org/scarlet/android/currency/livedata/CurrencyViewModel.kt)
- Flow
    - [android/currency/flow/CurrencyActivity.kt](app/src/main/java/org/scarlet/android/currency/flow/CurrencyActivity.kt)
    - [android/currency/flow/CurrencyViewModel.kt](app/src/main/java/org/scarlet/android/currency/flow/CurrencyViewModel.kt)

> #### How Flows Demo

- [android/hotflow/HotFlowActivity.kt](app/src/main/java/org/scarlet/android/hotflow/HotFlowActivity.kt)
- [android/hotflow/HotFlowViewModel.kt](app/src/main/java/org/scarlet/android/hotflow/HotFlowViewModel.kt)

***

### Channels

- [channel/ch01-basics.kt](app/src/main/java/org/scarlet/channel/ch01-basics.kt)
- [channel/ch02-produce.kt](app/src/main/java/org/scarlet/channel/ch02-produce.kt)
- [channel/ch03-pipeline.kt](app/src/main/java/org/scarlet/channel/ch03-pipeline.kt)
- [channel/ch04-fanIn-fanOut.kt](app/src/main/java/org/scarlet/channel/ch04-fanIn-fanOut.kt)
- [channel/ch05-buffer.kt](app/src/main/java/org/scarlet/channel/ch05-buffer.kt)
- [channel/ch06-fair.kt](app/src/main/java/org/scarlet/channel/ch06-fair.kt)
- [channel/ch07-broadcastChannel.kt](app/src/main/java/org/scarlet/channel/ch07-broadcastChannel.kt)
- [channel/ch08-actor.kt](app/src/main/java/org/scarlet/channel/ch08-actor.kt)
- [channel/ch09-selector.kt](app/src/main/java/org/scarlet/channel/ch09-selector.kt)

***

### Flows Basics

- [flows/basics/flow01-intro.kt](app/src/main/java/org/scarlet/flows/basics/flow01-intro.kt)
- [flows/basics/flow02-eagerVsLazy.kt](app/src/main/java/org/scarlet/flows/basics/flow02-eagerVsLazy.kt)
- [flows/basics/flow03-cold.kt](app/src/main/java/org/scarlet/flows/basics/flow03-cold.kt)
- [flows/basics/flow04-timeout.kt](app/src/main/java/org/scarlet/flows/basics/flow04-timeout.kt)
- [flows/basics/flow05-terminal-operators.kt](app/src/main/java/org/scarlet/flows/basics/flow05-terminal-operators.kt)
- [flows/basics/flow06-intermediate-operators.kt](app/src/main/java/org/scarlet/flows/basics/flow06-intermediate-operators.kt)
- [flows/basics/flow07-sequential.kt](app/src/main/java/org/scarlet/flows/basics/flow07-sequential.kt)
- [flows/basics/flow08-buffering.kt](app/src/main/java/org/scarlet/flows/basics/flow08-buffering.kt)
- [flows/basics/flow09-conflation.kt](app/src/main/java/org/scarlet/flows/basics/flow09-conflation.kt)
- [flows/basics/flow10-collectLatest.kt](app/src/main/java/org/scarlet/flows/basics/flow10-collectLatest.kt)

***

### Flows Advanced

- [flows/advanced/a1composition/flow11-zip.kt](app/src/main/java/org/scarlet/flows/advanced/a1composition/flow11-zip.kt)
- [flows/advanced/a2flattening/flow12-flatmapFamily.kt](app/src/main/java/org/scarlet/flows/advanced/a2flattening/flow12-flatmapFamily.kt)
- [flows/advanced/a3context/flow13-context-preservation.kt](app/src/main/java/org/scarlet/flows/advanced/a3context/flow13-context-preservation.kt)
- [flows/advanced/a3context/flow14-violation-of-context-preservation.kt](app/src/main/java/org/scarlet/flows/advanced/a3context/flow14-violation-of-context-preservation.kt)
- [flows/advanced/a3context/flow15-flowOn.kt](app/src/main/java/org/scarlet/flows/advanced/a3context/flow15-flowOn.kt)
- [flows/advanced/a3context/flow16-which-context.kt](app/src/main/java/org/scarlet/flows/advanced/a3context/flow16-which-context.kt)
- [flows/advanced/a4exceptions/flow17-tryCatch.kt](app/src/main/java/org/scarlet/flows/advanced/a4exceptions/flow17-tryCatch.kt)
- [flows/advanced/a4exceptions/flow18-violationOfExceptionTransparency.kt](app/src/main/java/org/scarlet/flows/advanced/a4exceptions/flow18-violationOfExceptionTransparency.kt)
- [flows/advanced/a4exceptions/flow19-exceptionTransparency.kt](app/src/main/java/org/scarlet/flows/advanced/a4exceptions/flow19-exceptionTransparency.kt)
- [flows/advanced/a5completion/flow20-completionStyle.kt](app/src/main/java/org/scarlet/flows/advanced/a5completion/flow20-completionStyle.kt)
- [flows/advanced/a6launching_and_cancellation/flow21-cancellation.kt](app/src/main/java/org/scarlet/flows/advanced/a6launching_and_cancellation/flow21-cancellation.kt)
- [flows/advanced/a6launching_and_cancellation/flow22-launchIn.kt](app/src/main/java/org/scarlet/flows/advanced/a6launching_and_cancellation/flow22-launchIn.kt)

***

### Genesis of Flows

- [flows/genesis/Genesis.kt](app/src/main/java/org/scarlet/flows/genesis/Genesis.kt)
- [flows/genesis/RealFlows.kt](app/src/main/java/org/scarlet/flows/genesis/RealFlows.kt)

***

### Hot Flows

- SharedFlow
    - [flows/hot/hot01-SharedFlow.kt](app/src/main/java/org/scarlet/flows/hot/hot01-SharedFlow.kt)
    - [flows/hot/hot02-shareIn.kt](app/src/main/java/org/scarlet/flows/hot/hot02-shareIn.kt)
- StateFlow
    - [flows/hot/hot03-StateFlow.kt](app/src/main/java/org/scarlet/flows/hot/hot03-StateFlow.kt)
    - [flows/hot/hot04-stateIn.kt](app/src/main/java/org/scarlet/flows/hot/hot04-stateIn.kt)
- SharedFlow as StateFlow
    - [flows/hot/hot05-SharedFlow-as-StateFlow.kt](app/src/main/java/org/scarlet/flows/hot/hot05-SharedFlow-as-StateFlow.kt)

***

### Migration from Callback to Flows

- Oneshot Request
    - [flows/migration/callbacks/oneshot/RecipeApi.kt](app/src/main/java/org/scarlet/flows/migration/callbacks/oneshot/RecipeApi.kt)
    - [flows/migration/callbacks/oneshot/Repository.kt](app/src/main/java/org/scarlet/flows/migration/callbacks/oneshot/Repository.kt)
    - [flows/migration/callbacks/oneshot/DefaultRepository.kt](app/src/main/java/org/scarlet/flows/migration/callbacks/oneshot/DefaultRepository.kt)
- Stream Request
    - [flows/migration/callbacks/stream/LocationService.kt](app/src/main/java/org/scarlet/flows/migration/callbacks/stream/LocationService.kt)
    - [flows/migration/callbacks/stream/FakeLocationService.kt](app/src/main/java/org/scarlet/flows/migration/callbacks/stream/FakeLocationService.kt)

### Migration From LiveData to Flows

- [flows/migration/viewmodeltoview/Repository.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/Repository.kt)
- [flows/migration/viewmodeltoview/AuthManager.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/AuthManager.kt)
- Case 1: Expose the result of a one-shot operation with a Mutable data holder
    - [flows/migration/viewmodeltoview/case1/ViewModelLive.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case1/ViewModelLive.kt)
    - [flows/migration/viewmodeltoview/case1/ViewModelFlow.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case1/ViewModelFlow.kt)
- Case 2: Expose the result of a one-shot operation without a mutable backing property
    - [flows/migration/viewmodeltoview/case2/ViewModelLive.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case2/ViewModelLive.kt)
    - [flows/migration/viewmodeltoview/case2/ViewModelFlow.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case2/ViewModelFlow.kt)
- Case 3: One-shot data load with parameters
    - [flows/migration/viewmodeltoview/case3/ViewModelLive.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case3/ViewModelLive.kt)
    - [flows/migration/viewmodeltoview/case3/ViewModelFlow.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case3/ViewModelFlow.kt)
- Case 4:  Observing a stream of data with parameters
    - [flows/migration/viewmodeltoview/case4/ViewModelLive.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case4/ViewModelLive.kt)
    - [flows/migration/viewmodeltoview/case4/ViewModelFlow.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case4/ViewModelFlow.kt)

***

### Utils

- [util/Utils.kt](app/src/main/java/org/scarlet/util/Utils.kt)
- [util/Resource.kt](app/src/main/java/org/scarlet/util/Resource.kt)
- [util/DispatchersProvider.kt](app/src/main/java/org/scarlet/util/DispatchersProvider.kt)

***

## Flows Testing

### Flows Testing Basics

- [flows/basics/DataSource.kt](app/src/test/java/org/scarlet/flows/basics/DataSource.kt)
- Native Test
    - [flows/basics/Flow01_Native_Test.kt](app/src/test/java/org/scarlet/flows/basics/Flow01_Native_Test.kt)
- Turbine Test
    - [flows/basics/Flow02_Turbine_Test.kt](app/src/test/java/org/scarlet/flows/basics/Flow02_Turbine_Test.kt)

### Utils for Testing

- LiveData Testing
    - [util/LiveDataTestUtil.kt](app/src/test/java/org/scarlet/util/LiveDataTestUtil.kt)
- Coroutine Test Rules
    - [flows/CoroutineTestRule.kt](app/src/test/java/org/scarlet/flows/CoroutineTestRule.kt)
- Test Dispatcher
    - [util/TestUtils.kt](app/src/test/java/org/scarlet/util/TestUtils.kt)

### Hot Flows Testing

- SharedFlow
    - [flows/hot/SharedFlow_NativeTest.kt](app/src/test/java/org/scarlet/flows/hot/SharedFlow_NativeTest.kt)
    - [flows/hot/SharedFlow_TurbineTest.kt](app/src/test/java/org/scarlet/flows/hot/SharedFlow_TurbineTest.kt)
- StateFlow
    - [flows/hot/StateFlow_NativeTest.kt](app/src/test/java/org/scarlet/flows/hot/StateFlow_NativeTest.kt)
    - [flows/hot/StateFlow_TurbineTest.kt](app/src/test/java/org/scarlet/flows/hot/StateFlow_TurbineTest.kt)

### Migration from Callback to Flows Testing

- Oneshot Request Test
    - [flows/migration/callbacks/oneshot/RecipeApi.kt](app/src/main/java/org/scarlet/flows/migration/callbacks/oneshot/RecipeApi.kt)
    - [flows/migration/callbacks/oneshot/Repository.kt](app/src/main/java/org/scarlet/flows/migration/callbacks/oneshot/Repository.kt)
    - [flows/migration/callbacks/oneshot/DefaultRepository.kt](app/src/main/java/org/scarlet/flows/migration/callbacks/oneshot/DefaultRepository.kt)
    - [flows/migration/callbacks/oneshot/RepositoryTest.kt](app/src/test/java/org/scarlet/flows/migration/callbacks/oneshot/RepositoryTest.kt)
- Stream Request Test
    - [flows/migration/callbacks/stream/LocationService.kt](app/src/main/java/org/scarlet/flows/migration/callbacks/stream/LocationService.kt)
    - [flows/migration/callbacks/stream/FakeLocationService.kt](app/src/main/java/org/scarlet/flows/migration/callbacks/stream/FakeLocationService.kt)
    - [flows/migration/callbacks/stream/LocationServiceTest.kt](app/src/test/java/org/scarlet/flows/migration/callbacks/stream/LocationServiceTest.kt)

### Migration from Callback to Flows Testing

- [flows/migration/viewmodeltoview/Repository.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/Repository.kt)
- [flows/migration/viewmodeltoview/AuthManager.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/AuthManager.kt)
- Case 1: Expose the result of a one-shot operation with a Mutable data holder
    - [flows/migration/viewmodeltoview/case1/ViewModelLive.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case1/ViewModelLive.kt)
    - [flows/migration/viewmodeltoview/case1/ViewModelLiveTest.kt](app/src/test/java/org/scarlet/flows/migration/viewmodeltoview/case1/ViewModelLiveTest.kt)
    - [flows/migration/viewmodeltoview/case1/ViewModelFlow.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case1/ViewModelFlow.kt)
    - [flows/migration/viewmodeltoview/case1/ViewModelFlowTest.kt](app/src/test/java/org/scarlet/flows/migration/viewmodeltoview/case1/ViewModelFlowTest.kt)
- Case 2: Expose the result of a one-shot operation without a mutable backing property
    - [flows/migration/viewmodeltoview/case2/ViewModelLive.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case2/ViewModelLive.kt)
    - [flows/migration/viewmodeltoview/case2/ViewModelLiveTest.kt](app/src/test/java/org/scarlet/flows/migration/viewmodeltoview/case2/ViewModelLiveTest.kt)
    - [flows/migration/viewmodeltoview/case2/ViewModelFlow.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case2/ViewModelFlow.kt)
    - [flows/migration/viewmodeltoview/case2/ViewModelFlowTest.kt](app/src/test/java/org/scarlet/flows/migration/viewmodeltoview/case2/ViewModelFlowTest.kt)
- Case 3: One-shot data load with parameters
    - [flows/migration/viewmodeltoview/case3/ViewModelLive.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case3/ViewModelLive.kt)
    - [flows/migration/viewmodeltoview/case3/ViewModelLiveTest.kt](app/src/test/java/org/scarlet/flows/migration/viewmodeltoview/case3/ViewModelLiveTest.kt)
    - [flows/migration/viewmodeltoview/case3/ViewModelFlow.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case3/ViewModelFlow.kt)
    - [flows/migration/viewmodeltoview/case3/ViewModelFlowTest.kt](app/src/test/java/org/scarlet/flows/migration/viewmodeltoview/case3/ViewModelFlowTest.kt)
- Case 4:  Observing a stream of data with parameters
    - [flows/migration/viewmodeltoview/case4/ViewModelLive.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case4/ViewModelLive.kt)
    - [flows/migration/viewmodeltoview/case4/ViewModelLiveTest.kt](app/src/test/java/org/scarlet/flows/migration/viewmodeltoview/case4/ViewModelLiveTest.kt)
    - [flows/migration/viewmodeltoview/case4/ViewModelFlow.kt](app/src/main/java/org/scarlet/flows/migration/viewmodeltoview/case4/ViewModelFlow.kt)
    - [flows/migration/viewmodeltoview/case4/ViewModelFlowTest.kt](app/src/test/java/org/scarlet/flows/migration/viewmodeltoview/case4/ViewModelFlowTest.kt)

