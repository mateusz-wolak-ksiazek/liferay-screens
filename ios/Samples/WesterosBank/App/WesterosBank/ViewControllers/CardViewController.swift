/**
* Copyright (c) 2000-present Liferay, Inc. All rights reserved.
*
* This library is free software; you can redistribute it and/or modify it under
* the terms of the GNU Lesser General Public License as published by the Free
* Software Foundation; either version 2.1 of the License, or (at your option)
* any later version.
*
* This library is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
*/
import UIKit

class CardViewController: UIViewController {

	var onDone: (() -> Void)?

	let cardView: CardView?

	init(card: CardView, nibName: String) {
		self.cardView = card

		super.init(nibName: nibName, bundle: nil)

		self.view.frame = CGRect(
				x: 0,
				y: card.minimizedHeight,
				width: card.frame.width,
				height: card.frame.height)

		card.autoresizesSubviews = true
		self.view.autoresizesSubviews = true
		self.view.autoresizingMask =  [.flexibleWidth, .flexibleHeight]

		card.addSubview(self.view)

		card.presentingController = self
	}

	required init?(coder aDecoder: NSCoder) {
		self.cardView = nil

		super.init(coder: aDecoder)
	}

	func cardWillAppear() {
	}

	func cardWillDisappear() {
	}

}
