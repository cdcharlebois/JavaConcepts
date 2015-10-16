// This file was generated by Mendix Business Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package objectcomparator.actions;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import objectcomparator.proxies.Comparable;
import objectcomparator.proxies.MisMatch;
import com.mendix.core.Core;
import com.mendix.core.objectmanagement.member.MendixObjectReference;
import com.mendix.core.objectmanagement.member.MendixObjectReferenceSet;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.IMendixObjectMember;
import com.mendix.webui.CustomJavaAction;
import com.mendix.webui.FeedbackHelper;

/**
 * 
 */
public class Compare extends CustomJavaAction<java.util.List<IMendixObject>>
{
	private IMendixObject __ComparableTarget;
	private objectcomparator.proxies.Comparable ComparableTarget;
	private IMendixObject __ComparatorSource;
	private objectcomparator.proxies.Comparator ComparatorSource;

	public Compare(IContext context, IMendixObject ComparableTarget, IMendixObject ComparatorSource)
	{
		super(context);
		this.__ComparableTarget = ComparableTarget;
		this.__ComparatorSource = ComparatorSource;
	}

	@Override
	public java.util.List<IMendixObject> executeAction() throws Exception
	{
		this.ComparableTarget = __ComparableTarget == null ? null : objectcomparator.proxies.Comparable.initialize(getContext(), __ComparableTarget);

		this.ComparatorSource = __ComparatorSource == null ? null : objectcomparator.proxies.Comparator.initialize(getContext(), __ComparatorSource);

		// BEGIN USER CODE

		ArrayList<IMendixObject> mismatches = new ArrayList<>();
		if( this.ComparatorSource != null ) {
			
			Map<String, ? extends IMendixObjectMember<?>> members = this.ComparatorSource.getMendixObject().getMembers(getContext());
			
			for(Entry<String, ? extends IMendixObjectMember<?>> entry : members.entrySet() ) {
				String memberName = entry.getKey();
				IMendixObjectMember<?> member = entry.getValue();
				
				if( member.isVirtual() || memberName.contains(".") || "ObjectType".equals(memberName) || 
					"SourceType".equals(memberName) || 
					member instanceof MendixObjectReference || member instanceof MendixObjectReferenceSet ) 
					continue;
				
				Object ComparatorValue = member.getValue(getContext());
				Object ComparableValue = null;

				boolean mismatch = false, isAutoGenerated = true; 
				if( this.__ComparableTarget != null ) {
					isAutoGenerated = (boolean) this.__ComparableTarget.getValue(getContext(), Comparable.MemberNames.AutoGenerated.toString());
					if( this.__ComparableTarget.hasMember(memberName) ) 
						ComparableValue = this.__ComparableTarget.getValue(getContext(), memberName);
				}
				
				if( isAutoGenerated || 
					ComparatorValue == null && ComparatorValue != ComparableValue || 
					ComparatorValue != null && !ComparatorValue.equals(ComparableValue) )
					mismatch = true;
				
				if( mismatch ) {
					IMendixObject mismatchObj = Core.instantiate(getContext(), MisMatch.entityName);
					mismatchObj.setValue(getContext(), MisMatch.MemberNames.FieldName.toString(), memberName);
					mismatchObj.setValue(getContext(), MisMatch.MemberNames.ComparatorValue.toString(), String.valueOf(ComparatorValue) );
					mismatchObj.setValue(getContext(), MisMatch.MemberNames.ComparableValue.toString(), (isAutoGenerated ? "" : String.valueOf(ComparableValue)) );
					mismatchObj.setValue(getContext(), MisMatch.MemberNames.MisMatch_Comparator.toString(), this.__ComparatorSource.getId());
					if( this.__ComparableTarget != null ) {
						mismatchObj.setValue(getContext(), MisMatch.MemberNames.MisMatch_Comparable.toString(), this.__ComparableTarget.getId());
						mismatchObj.setValue(getContext(), MisMatch.MemberNames.MisMatch_Source.toString(), this.__ComparableTarget.getValue(getContext(), Comparable.MemberNames.Comparable_Source.toString()));
					}
					
					Core.commit(getContext(), mismatchObj);
					getContext().getSession().retain(mismatchObj);
					mismatches.add(mismatchObj);
				}
			}
			
		}

		FeedbackHelper.addRefreshClass(getContext(), MisMatch.entityName);
		FeedbackHelper.addRefreshClass(getContext(), Comparable.entityName);
		
		return mismatches;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public String toString()
	{
		return "Compare";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
