import FBReaderSDK

class TextBookDataHolderImpl: TextBookDataHolder {
  let book: Book
  
  init(book: Book) {
    self.book = book
  }

  var position: TextPosition? = nil
  
  func save(position: TextPosition, progress: RationalNumber) {
    self.position = position
  }
  
  func highlightingStyle(_ id: Int) -> HighlightingStyle? {
    return HighlightingStyle(id: id, fgColor: nil, bgColor: 0xA00000, savedName: "Red", timestamp: 0)
  }
  
  private var _bookmarks = [Bookmark]()
  func bookmarks(for modelId: String?) -> [Bookmark] {
    return self._bookmarks.filter {$0.modelId == modelId}
  }

  func save(bookmark: Bookmark) -> Bookmark? {
    self._bookmarks.append(bookmark)
    return bookmark
  }

  var recentLocations = [TextLocation]()
  
  func save(recentLocations: [TextLocation]) {
    self.recentLocations = recentLocations
  }
}


@objc(FBReaderViewManager)
class FBReaderViewManager: RCTViewManager {

  override func view() -> (FBReaderView) {
    return FBReaderView()
  }
  
  @objc override static func requiresMainQueueSetup() -> Bool {
      return false
  }
}

class FBReaderView : UIView, TextWidgetDelegate {
  private var textWidget: TextWidget? = nil

  @objc var book: String = "" {
    didSet {
      Options.loadDefaults()
      
      let documentsDirectory = getDocumentsDirectory()
      let bookPath = book.starts(with: documentsDirectory) ? String(book[documentsDirectory.endIndex..<book.endIndex]) : book

      if let widget = getTextWidget(), let book = BookLoader.book(from: bookPath, embedded: false) {
        widget.open(book: book, withProgressIndicator: nil) {
          success in
          widget.rebuildPaintInfo()
          widget.setNeedsDisplay()
        }
      }
    }
  }
  
  @objc var background = "" {
    didSet {
      if let widget = getTextWidget() {
        // TODO
        widget.forceUpdateTextParams()
        widget.rebuildPaintInfo()
        widget.setNeedsDisplay()
      }
    }
  }
  
  @objc var colorProfile = "" {
    didSet {
      if let widget = getTextWidget() {
        Options.colorProfileName = colorProfile
        widget.forceUpdateTextParams()
        widget.rebuildPaintInfo()
        widget.setNeedsDisplay()
      }
    }
  }
  
  @objc var fontSize = 10 {
    didSet {
      if let widget = getTextWidget() {
        Options.Reading.baseFontSize.value = fontSize
        widget.forceUpdateTextParams()
        widget.rebuildPaintInfo()
        widget.setNeedsDisplay()
      }
    }
  }

  @objc var searchInText = "" {
    didSet {
      if let widget = getTextWidget() {
        let _ = widget.search(pattern: searchInText, withProgressIndicator: nil)
      }
    }
  }
  
  @objc var tocReference = 0 {
    didSet {
      if let widget = getTextWidget() {
        
      }
    }
  }
  
  @objc var page = 0 {
    didSet {
      if let widget = getTextWidget() {
        widget.goto(pageNo: page)
      }
    }
  }
  
  override func layoutSublayers(of layer: CALayer) {
    super.layoutSublayers(of: layer)
    if let widget = getTextWidget() {
      widget.frame = self.bounds
      widget.backgroundColor = UIColor.red
      self.backgroundColor = UIColor.green
    }
  }
  
  func getTextWidget() -> TextWidget? {
    if textWidget == nil {
      textWidget = TextWidget()
      textWidget?.delegate = self
      textWidget?.autoresizingMask = [.flexibleWidth, .flexibleHeight]
      addSubview(textWidget!)
    }
    return textWidget
  }
  
  func getDocumentsDirectory() -> String {
    let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
    let documentsDirectory = paths[0]
    return documentsDirectory.path
  }
  

//
  func onTap(_ pt: CGPoint) {
    if let widget = getTextWidget() {
      if pt.x <= widget.frame.width / 2 {
        widget.turnBack(animated: true)
      } else if pt.x >= widget.frame.width / 2 {
        widget.turnForward(animated: true)
      }
    }
  }
  
  func onSelectionChanged(_ pt: CGPoint) {
  }
  func onSelectionCleared() {
  }

  func hidePopupElements() -> Bool {
    return false
  }
  
  func followFootnote(id: String, sourceRect: CGRect, openFootnoteHandler: @escaping (() -> Void)) {
  }
  
  func onFootnoteShown() {
  }
  
  func onCreated(bookmark: Bookmark, in rect: CGRect) {
  }
  
  func onTouched(bookmark: Bookmark, in rect: CGRect) {
  }
  
  func onLongTouchInVoiceOverMode() {
  }
  
  func openSelectionMenu(_ pt: CGPoint) {
  }
  
  func createDataHolder(book: Book) -> TextBookDataHolder {
    return TextBookDataHolderImpl(book: book)
  }
  
}
