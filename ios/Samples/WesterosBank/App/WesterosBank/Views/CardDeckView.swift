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

class CardDeckView: UIView {

	var topCard: CardView? {
		didSet {
			if let cardValue = topCard {
				setUpCard(cardValue)

				addButton(cardValue, fontColor: Colors.mainRed)

				cardValue.layer.zPosition = -layer.bounds.size.width
			}
		}
	}

	var bottomCard: CardView? {
		didSet {
			if let cardValue = bottomCard {
				setUpCard(cardValue)

				topCard?.minimizedHeight += cardValue.minimizedHeight

				addButton(cardValue, fontColor: .white)
			}
		}
	}

	var onButtonTouched: ((CardView) -> Void)?

	func setUpCard(_ card: CardView) {
		card.currentState = .minimized
		card.nextState = .normal

		card.layer.cornerRadius = 4.0
	}

	func addButton(_ card: CardView, fontColor: UIColor) {
		let actionName = card === topCard
				? "topCardTouchUpInside:" : "bottomCardTouchUpInside:"

		card.createButton(fontColor)
				.addTarget(self,
					action: Selector(actionName),
					for: .touchUpInside)

		card.createArrow(fontColor)
	}

	func topCardTouchUpInside(_ sender: UIButton) {
		onButtonTouched?(topCard!)
	}

	func bottomCardTouchUpInside(_ sender: UIButton) {
		onButtonTouched?(bottomCard!)
	}

}
