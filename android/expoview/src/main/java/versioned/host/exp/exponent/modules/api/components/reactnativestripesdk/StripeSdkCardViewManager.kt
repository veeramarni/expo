package versioned.host.exp.exponent.modules.api.components.reactnativestripesdk

import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableNativeMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.modules.core.ExceptionsManagerModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

const val CARD_FIELD_INSTANCE_NAME = "CardFieldInstance"

class StripeSdkCardViewManager : SimpleViewManager<StripeSdkCardView>() {
  override fun getName() = "CardField"

  private var cardViewInstanceMap: MutableMap<String, Any?> = mutableMapOf()

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any> {
    return MapBuilder.of(
      CardFocusEvent.EVENT_NAME, MapBuilder.of("registrationName", "onFocusChange"),
      CardChangedEvent.EVENT_NAME, MapBuilder.of("registrationName", "onCardChange"))
  }

  @ReactProp(name = "postalCodeEnabled")
  fun setPostalCodeEnabled(view: StripeSdkCardView, postalCodeEnabled: Boolean = true) {
    view.setPostalCodeEnabled(postalCodeEnabled);
  }

  @ReactProp(name = "cardStyle")
  fun setCardStyle(view: StripeSdkCardView, cardStyle: ReadableMap) {
    view.setCardStyle(cardStyle);
  }

  @ReactProp(name = "placeholder")
  fun setPlaceHolders(view: StripeSdkCardView, placeholder: ReadableMap) {
    view.setPlaceHolders(placeholder);
  }

  override fun createViewInstance(reactContext: ThemedReactContext): StripeSdkCardView {
    // as it's reasonable we handle only one CardField component on the same screen
    if (cardViewInstanceMap[CARD_FIELD_INSTANCE_NAME] != null) {
      val exceptionManager = reactContext.getNativeModule(ExceptionsManagerModule::class.java)
      val error: WritableMap = WritableNativeMap()
      error.putString("message", "Only one CardField component on the same screen allowed")
      exceptionManager?.reportException(error)
    }

    cardViewInstanceMap[CARD_FIELD_INSTANCE_NAME] = StripeSdkCardView(reactContext)
    return cardViewInstanceMap[CARD_FIELD_INSTANCE_NAME] as StripeSdkCardView
  }

  override fun onDropViewInstance(view: StripeSdkCardView) {
    super.onDropViewInstance(view)

    this.cardViewInstanceMap[CARD_FIELD_INSTANCE_NAME] = null
  }

  fun getCardViewInstance(): StripeSdkCardView? {
    if (cardViewInstanceMap[CARD_FIELD_INSTANCE_NAME] != null) {
      return cardViewInstanceMap[CARD_FIELD_INSTANCE_NAME] as StripeSdkCardView
    }
    return null
  }
}
