import FBReaderSDK

@objc(FBReader)
class FBReader: NSObject {
  
  @objc static func requiresMainQueueSetup() -> Bool {
      return false
  }

  @objc(tableOfContents:withResolver:withRejecter:)
  func tableOfContents(book: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    let widget = TextWidget()
    let documentsDirectory = FBReaderView.getDocumentsDirectory()
    let bookPath = book.starts(with: documentsDirectory) ? String(book[documentsDirectory.endIndex..<book.endIndex]) : book

    if let book = BookLoader.book(from: bookPath, embedded: false) {
      widget.open(book: book, withProgressIndicator: nil) {
        success in
        let tocMap = self.toMap(node: widget.tableOfContentsTree)
        resolve(tocMap)
      }
    }
  }
  
  func toMap(node: ToCTree?) -> [String: Any] {
    var map = [String: Any]()
    if let title = node?.title, let ref = node?.reference {
      map["title"] = title
      map["ref"] = ref
    }
    
    if let children = node?.children {
      var lst = [Dictionary<String, Any>]()
      for child in children {
        lst.append(toMap(node: child))
      }
      if !lst.isEmpty {
        map["children"] = lst
      }
    }
    return map
  }
}
